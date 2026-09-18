package com.devtools.ai.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorParserTest {

    private ErrorParser errorParser;

    @BeforeEach
    void setUp() {
        errorParser = new ErrorParser(new SecretSanitizer());
    }

    @Test
    void shouldDetectHibernateLazyInitializationException() {
        String trace = """
                org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.model.Customer.orders: could not initialize proxy - no Session
                \tat org.hibernate.collection.spi.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:631)
                \tat com.example.service.CustomerService.calculateLoyaltyTier(CustomerService.java:48)
                """;

        ProcessedErrorInput result = errorParser.process(trace);
        assertEquals("LazyInitializationException", result.getDetectedException());
        assertFalse(result.isWasSanitized());
        assertFalse(result.isWasTruncated());
    }

    @Test
    void shouldDetectBeanCreationExceptionAndCausedByChain() {
        String trace = """
                org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'orderService': Injection of autowired dependencies failed
                \tat org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessProperties(AutowiredAnnotationBeanPostProcessor.java:515)
                Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.example.service.PaymentGateway' available
                \tat org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1880)
                """;

        ProcessedErrorInput result = errorParser.process(trace);
        assertEquals("BeanCreationException", result.getDetectedException());
        List<String> chain = result.getCausedByChain();
        assertEquals(1, chain.size());
        assertTrue(chain.get(0).contains("NoSuchBeanDefinitionException"));
    }

    @Test
    void shouldCondenseVeryLargeStackTrace() {
        StringBuilder largeTrace = new StringBuilder("java.lang.RuntimeException: Big Error\n");
        for (int i = 0; i < 600; i++) {
            largeTrace.append("\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:100)\n");
        }
        largeTrace.append("Caused by: java.lang.NullPointerException: Target is null\n");

        ProcessedErrorInput result = errorParser.process(largeTrace.toString());
        assertTrue(result.isWasTruncated());
        assertTrue(result.getNormalizedText().length() < largeTrace.length());
        assertTrue(result.getNormalizedText().contains("omitted"));
    }
}
