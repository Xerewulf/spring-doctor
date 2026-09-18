package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/examples")
public class ExamplesController {

    private static final List<Map<String, String>> EXAMPLES = List.of(
            Map.of(
                    "id", "lazy-init",
                    "name", "Hibernate LazyInitializationException",
                    "technology", "SPRING_BOOT",
                    "description", "Could not initialize proxy - no Session (accessing lazy collection outside transaction)",
                    "errorText", """
org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.model.Customer.orders: could not initialize proxy - no Session
\tat org.hibernate.collection.spi.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:631) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:270) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.AbstractPersistentCollection.readSize(AbstractPersistentCollection.java:191) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.PersistentBag.size(PersistentBag.java:391) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat com.example.service.CustomerService.calculateLoyaltyTier(CustomerService.java:48) ~[classes/:na]
\tat com.example.controller.CustomerController.getCustomerProfile(CustomerController.java:34) ~[classes/:na]
\tat java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
\tat java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:255) ~[spring-web-6.1.12.jar:6.1.12]
"""
            ),
            Map.of(
                    "id", "bean-creation",
                    "name", "Spring BeanCreationException",
                    "technology", "SPRING_BOOT",
                    "description", "Unsatisfied dependency expressed through constructor parameter; no qualifying bean of type",
                    "errorText", """
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'orderProcessingController' defined in file [/app/target/classes/com/example/controller/OrderProcessingController.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'paymentService': No qualifying bean of type 'com.example.service.PaymentGateway' available: expected at least 1 bean which qualifies as autowire candidate. Dependency keys: [paymentService]
\tat org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:800) ~[spring-beans-6.1.12.jar:6.1.12]
\tat org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:245) ~[spring-beans-6.1.12.jar:6.1.12]
\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1372) ~[spring-beans-6.1.12.jar:6.1.12]
\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1212) ~[spring-beans-6.1.12.jar:6.1.12]
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.example.service.PaymentGateway' available: expected at least 1 bean which qualifies as autowire candidate.
\tat org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1880) ~[spring-beans-6.1.12.jar:6.1.12]
\tat org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1406) ~[spring-beans-6.1.12.jar:6.1.12]
"""
            ),
            Map.of(
                    "id", "postgres-connection",
                    "name", "PostgreSQL Connection Refused",
                    "technology", "POSTGRESQL",
                    "description", "HikariPool-1 - Connection is not available, request timed out after 30000ms",
                    "errorText", """
org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection
\tat org.springframework.jdbc.datasource.DataSourceUtils.getConnection(DataSourceUtils.java:84) ~[spring-jdbc-6.1.12.jar:6.1.12]
\tat org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:389) ~[spring-jdbc-6.1.12.jar:6.1.12]
Caused by: com.zaxxer.hikari.pool.HikariPool$PoolInitializationException: Exception during pool initialization: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
\tat com.zaxxer.hikari.pool.HikariPool.throwPoolInitializationException(HikariPool.java:596) ~[HikariCP-5.1.0.jar:na]
\tat com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:573) ~[HikariCP-5.1.0.jar:na]
Caused by: org.postgresql.util.PSQLException: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
\tat org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:342) ~[postgresql-42.7.4.jar:42.7.4]
\tat org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:54) ~[postgresql-42.7.4.jar:42.7.4]
\tat org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:273) ~[postgresql-42.7.4.jar:42.7.4]
"""
            ),
            Map.of(
                    "id", "npe",
                    "name", "NullPointerException",
                    "technology", "JAVA",
                    "description", "Cannot invoke method because obj is null",
                    "errorText", """
java.lang.NullPointerException: Cannot invoke "com.example.model.Address.getZipCode()" because the return value of "com.example.model.User.getAddress()" is null
\tat com.example.service.ShippingCalculator.calculateTax(ShippingCalculator.java:42) ~[classes/:na]
\tat com.example.service.CheckoutService.processCheckout(CheckoutService.java:89) ~[classes/:na]
\tat com.example.controller.OrderController.submitOrder(OrderController.java:56) ~[classes/:na]
\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:255) ~[spring-web-6.1.12.jar:6.1.12]
"""
            ),
            Map.of(
                    "id", "security-403",
                    "name", "Spring Security 403 Access Denied",
                    "technology", "SPRING_BOOT",
                    "description", "Access Denied: User has authorities [ROLE_USER] but requires [ROLE_ADMIN]",
                    "errorText", """
org.springframework.security.access.AccessDeniedException: Access Denied: User has authorities [ROLE_USER] but requires [ROLE_ADMIN]
\tat org.springframework.security.access.intercept.AbstractSecurityInterceptor.beforeInvocation(AbstractSecurityInterceptor.java:208) ~[spring-security-core-6.3.3.jar:6.3.3]
\tat org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor.invoke(MethodSecurityInterceptor.java:58) ~[spring-security-core-6.3.3.jar:6.3.3]
\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.12.jar:6.1.12]
\tat com.example.controller.AdminUserController.deleteUser(AdminUserController.java:78) ~[classes/:na]
"""
            ),
            Map.of(
                    "id", "feign-timeout",
                    "name", "Feign Client Timeout",
                    "technology", "SPRING_BOOT",
                    "description", "Read timed out executing GET http://inventory-service/api/stock/1001",
                    "errorText", """
feign.RetryableException: Read timed out executing GET http://inventory-service/api/v1/stock/1001
\tat feign.FeignException.errorExecuting(FeignException.java:268) ~[feign-core-13.3.jar:na]
\tat feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:131) ~[feign-core-13.3.jar:na]
\tat feign.SynchronousMethodHandler.invoke(SynchronousMethodHandler.java:91) ~[feign-core-13.3.jar:na]
\tat feign.ReflectiveFeign$FeignInvocationHandler.invoke(ReflectiveFeign.java:100) ~[feign-core-13.3.jar:na]
\tat jdk.proxy2/$Proxy124.checkAvailability(Unknown Source) ~[na:na]
Caused by: java.net.SocketTimeoutException: Read timed out
\tat java.base/sun.nio.ch.NioSocketImpl.timedRead(NioSocketImpl.java:278) ~[na:na]
\tat java.base/sun.nio.ch.NioSocketImpl.implRead(NioSocketImpl.java:304) ~[na:na]
"""
            ),
            Map.of(
                    "id", "data-integrity",
                    "name", "DataIntegrityViolationException",
                    "technology", "HIBERNATE",
                    "description", "duplicate key value violates unique constraint 'uk_users_email'",
                    "errorText", """
org.springframework.dao.DataIntegrityViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint "uk_users_email"
  Detail: Key (email)=(alex.developer@company.com) already exists.] [insert into users (email,first_name,last_name,password_hash,plan) values (?,?,?,?,?)]; SQL [insert into users (email,first_name,last_name,password_hash,plan) values (?,?,?,?,?)]
\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:290) ~[spring-orm-6.1.12.jar:6.1.12]
\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:241) ~[spring-orm-6.1.12.jar:6.1.12]
\tat com.example.service.UserService.createUser(UserService.java:62) ~[classes/:na]
Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint "uk_users_email"]
"""
            )
    );

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getExampleErrors() {
        return ResponseEntity.ok(ApiResponse.ok(EXAMPLES));
    }
}
