package com.devtools.ai.service.ai;

import com.devtools.ai.ai.GeminiClient;
import com.devtools.ai.config.GeminiProperties;
import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.dto.SuggestedFixDto;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.parser.ProcessedErrorInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAnalysisService implements AIAnalysisService {

    private final GeminiClient geminiClient;
    private final GeminiPromptService promptService;
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    @Value("${devtools.ai.fallback-to-mock-if-no-key:true}")
    private boolean fallbackToMockIfNoKey;

    @Override
    public DiagnosisDto analyzeError(ProcessedErrorInput processedInput, String technology, String context) {
        String apiKey = geminiProperties.getApiKey();
        boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty() && !"null".equalsIgnoreCase(apiKey);

        if (!hasApiKey && fallbackToMockIfNoKey) {
            log.warn("Gemini API key is not configured. Using deterministic fallback analyzer for {}", processedInput.getDetectedException());
            return generateMockDiagnosis(processedInput, technology);
        }

        String prompt = promptService.generateErrorAnalysisPrompt(processedInput, technology, context);

        try {
            String jsonOutput = geminiClient.generateContent(prompt);
            jsonOutput = cleanJsonOutput(jsonOutput);

            DiagnosisDto diagnosis = objectMapper.readValue(jsonOutput, DiagnosisDto.class);

            // Populate metadata
            diagnosis.setDetectedException(processedInput.getDetectedException());
            if (processedInput.isWasSanitized()) {
                diagnosis.setSanitizationNotice(
                        "Sensitive data redacted before analysis: " + String.join(", ", processedInput.getRedactedCategories())
                );
            }

            return diagnosis;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini JSON output: {}", e.getMessage());
            throw new ApiException("AI returned an unparseable response format. Please retry.", HttpStatus.BAD_GATEWAY, "AI_INVALID_JSON");
        } catch (Exception e) {
            if (fallbackToMockIfNoKey) {
                log.error("Gemini call failed with error: {}. Falling back to deterministic analysis.", e.getMessage());
                return generateMockDiagnosis(processedInput, technology);
            }
            throw e;
        }
    }

    private String cleanJsonOutput(String output) {
        if (output == null) return "{}";
        String trimmed = output.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private DiagnosisDto generateMockDiagnosis(ProcessedErrorInput input, String technology) {
        String exc = input.getDetectedException();
        String notice = input.isWasSanitized()
                ? "Sensitive data redacted before analysis: " + String.join(", ", input.getRedactedCategories())
                : null;

        if ("LazyInitializationException".equalsIgnoreCase(exc)) {
            return DiagnosisDto.builder()
                    .summary("Your application is attempting to access a lazily loaded Hibernate collection or proxy after the persistence context (Session) has closed.")
                    .rootCause("Hibernate Session closed before proxy initialization. The entity collection is marked as FetchType.LAZY and was accessed in a web layer or non-transactional service boundary.")
                    .confidence("HIGH")
                    .severity("MEDIUM")
                    .whyItHappens("In Spring/Hibernate architectures, lazy collections use CGLIB/ByteBuddy proxies that require an active database Session to load data on demand. Once the @Transactional boundary exits or the HTTP request view rendering begins (without OpenSessionInView), the underlying Session closes, making further queries impossible.")
                    .suggestedFixes(List.of(
                            SuggestedFixDto.builder()
                                    .title("Fetch Join using @EntityGraph")
                                    .description("Explicitly join-fetch the required relationship in your Spring Data JPA repository query so the collection is populated eagerly for this query only.")
                                    .code("""
@EntityGraph(attributePaths = {"orders", "orders.items"})
@Query("SELECT u FROM User u WHERE u.id = :id")
Optional<User> findByIdWithOrders(@Param("id") Long id);
""")
                                    .language("java")
                                    .build(),
                            SuggestedFixDto.builder()
                                    .title("Wrap caller in @Transactional(readOnly = true)")
                                    .description("Keep the Hibernate Session active throughout the duration of your service method.")
                                    .code("""
@Service
public class UserService {
    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        // Accessing lazy collection while transaction is open
        return new UserDto(user.getId(), user.getOrders().size());
    }
}
""")
                                    .language("java")
                                    .build()
                    ))
                    .thingsToCheck(List.of(
                            "Check whether the entity is accessed outside the service @Transactional boundary.",
                            "Verify if the collection is configured as FetchType.LAZY on the entity mapping.",
                            "Check whether you can use DTO projection rather than serializing entities directly to JSON.",
                            "Avoid enable_lazy_load_no_trans as an anti-pattern (it leads to N+1 query cascades)."
                    ))
                    .relatedTechnologies(List.of("Spring Boot", "Hibernate", "Spring Data JPA"))
                    .possibleCauses(List.of(
                            "Direct entity serialization by Jackson in Spring MVC controller without transaction.",
                            "Async thread or background worker accessing detached entity."
                    ))
                    .detectedException(exc)
                    .sanitizationNotice(notice)
                    .build();
        } else if ("BeanCreationException".equalsIgnoreCase(exc) || "UnsatisfiedDependencyException".equalsIgnoreCase(exc)) {
            return DiagnosisDto.builder()
                    .summary("Spring IoC container failed to instantiate a bean due to an unsatisfied dependency or configuration error during startup.")
                    .rootCause("Unsatisfied dependency or missing @Component / @Service annotation on the target bean, or circular reference between spring components.")
                    .confidence("HIGH")
                    .severity("CRITICAL")
                    .whyItHappens("During ApplicationContext initialization, Spring parses bean definitions and constructs the dependency graph. If an injected interface has 0 implementations (or multiple ambiguous candidates without @Qualifier / @Primary), or if there is a cycle, the container aborts startup.")
                    .suggestedFixes(List.of(
                            SuggestedFixDto.builder()
                                    .title("Annotate target class with @Service or @Component")
                                    .description("Ensure the implementation class is detected by Spring component scanning.")
                                    .code("""
@Service
public class PaymentServiceImpl implements PaymentService {
    // implementation
}
""")
                                    .language("java")
                                    .build(),
                            SuggestedFixDto.builder()
                                    .title("Use @Qualifier or @Primary for ambiguous candidates")
                                    .description("If multiple beans implement the interface, specify which bean should be injected.")
                                    .code("""
@Autowired
public OrderService(@Qualifier("stripePaymentService") PaymentService paymentService) {
    this.paymentService = paymentService;
}
""")
                                    .language("java")
                                    .build()
                    ))
                    .thingsToCheck(List.of(
                            "Check if the class package is covered by @ComponentScan or root @SpringBootApplication.",
                            "Inspect application startup logs for 'No qualifying bean of type' messages.",
                            "Check for circular dependencies (consider constructor injection and redesigning components)."
                    ))
                    .relatedTechnologies(List.of("Spring Boot", "Spring IoC", "Spring Core"))
                    .possibleCauses(List.of(
                            "Class is in a package not scanned by @SpringBootApplication.",
                            "Missing conditional bean configuration or inactive Spring profile."
                    ))
                    .detectedException(exc)
                    .sanitizationNotice(notice)
                    .build();
        } else if ("PSQLException".equalsIgnoreCase(exc) || "SQLException".equalsIgnoreCase(exc)) {
            return DiagnosisDto.builder()
                    .summary("PostgreSQL connection refused or query execution failed in HikariCP datasource.")
                    .rootCause("Database network connection failure or credentials rejected by PostgreSQL server.")
                    .confidence("HIGH")
                    .severity("CRITICAL")
                    .whyItHappens("The Spring Boot HikariCP connection pool attempted to establish a TCP socket connection with PostgreSQL but received connection refused or authentication rejection.")
                    .suggestedFixes(List.of(
                            SuggestedFixDto.builder()
                                    .title("Verify PostgreSQL Container and Connection URL")
                                    .description("Confirm PostgreSQL is running on the host/port and update application.yml or environment variables.")
                                    .code("""
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/devtools_ai
    username: postgres
    password: ${DATABASE_PASSWORD}
""")
                                    .language("yaml")
                                    .build()
                    ))
                    .thingsToCheck(List.of(
                            "Verify if PostgreSQL container is running: docker ps",
                            "Verify host port 5432 is published and not blocked by firewall",
                            "Check pg_hba.conf if connecting across Docker network bridges"
                    ))
                    .relatedTechnologies(List.of("PostgreSQL", "HikariCP", "Spring Boot", "Docker"))
                    .possibleCauses(List.of(
                            "Database server container is not started.",
                            "Wrong credentials or database name."
                    ))
                    .detectedException(exc)
                    .sanitizationNotice(notice)
                    .build();
        }

        // Generic Java / Spring error fallback
        return DiagnosisDto.builder()
                .summary("An exception occurred during application execution: " + (input.getRootCauseMessage() != null ? input.getRootCauseMessage() : exc))
                .rootCause("Runtime failure: " + exc + " triggered in application logic or framework pipeline.")
                .confidence("MEDIUM")
                .severity("MEDIUM")
                .whyItHappens("The error was thrown during runtime execution. Check the top stack frames for the exact offending line in your application package.")
                .suggestedFixes(List.of(
                        SuggestedFixDto.builder()
                                .title("Null-check or defensive validation")
                                .description("Add defensive validation or null checks before dereferencing variables.")
                                .code("""
if (entity != null) {
    process(entity);
} else {
    log.warn("Expected entity was null");
}
""")
                                .language("java")
                                .build()
                ))
                .thingsToCheck(List.of(
                        "Inspect the first line in the stack trace matching your project's root package.",
                        "Verify input parameters and nullability annotations (@NonNull / @Nullable)."
                ))
                .relatedTechnologies(List.of(technology != null ? technology : "Java", "Spring Boot"))
                .possibleCauses(List.of("Unexpected null argument or unhandled state."))
                .detectedException(exc)
                .sanitizationNotice(notice)
                .build();
    }
}
