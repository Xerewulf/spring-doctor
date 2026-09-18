import { ExampleError } from '../types';

export const EXAMPLE_ERRORS: ExampleError[] = [
  {
    id: 'lazy-init',
    name: 'Hibernate LazyInitializationException',
    technology: 'SPRING_BOOT',
    description: 'Could not initialize proxy - no Session (accessing lazy collection outside transaction)',
    errorText: `org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.model.Customer.orders: could not initialize proxy - no Session
\tat org.hibernate.collection.spi.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:631) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:270) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.AbstractPersistentCollection.readSize(AbstractPersistentCollection.java:191) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat org.hibernate.collection.spi.PersistentBag.size(PersistentBag.java:391) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
\tat com.example.service.CustomerService.calculateLoyaltyTier(CustomerService.java:48) ~[classes/:na]
\tat com.example.controller.CustomerController.getCustomerProfile(CustomerController.java:34) ~[classes/:na]`
  },
  {
    id: 'bean-creation',
    name: 'Spring BeanCreationException',
    technology: 'SPRING_BOOT',
    description: 'Unsatisfied dependency expressed through constructor parameter; no qualifying bean of type',
    errorText: `org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'orderProcessingController' defined in file [/app/target/classes/com/example/controller/OrderProcessingController.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'paymentService': No qualifying bean of type 'com.example.service.PaymentGateway' available: expected at least 1 bean which qualifies as autowire candidate. Dependency keys: [paymentService]
\tat org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:800) ~[spring-beans-6.1.12.jar:6.1.12]
\tat org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:245) ~[spring-beans-6.1.12.jar:6.1.12]
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.example.service.PaymentGateway' available: expected at least 1 bean which qualifies as autowire candidate.
\tat org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1880) ~[spring-beans-6.1.12.jar:6.1.12]`
  },
  {
    id: 'postgres-conn',
    name: 'PostgreSQL Connection Refused',
    technology: 'POSTGRESQL',
    description: 'HikariCP failed to obtain JDBC Connection - Connection to localhost:5432 refused',
    errorText: `org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection
\tat org.springframework.jdbc.datasource.DataSourceUtils.getConnection(DataSourceUtils.java:84) ~[spring-jdbc-6.1.12.jar:6.1.12]
Caused by: com.zaxxer.hikari.pool.HikariPool$PoolInitializationException: Exception during pool initialization: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
\tat com.zaxxer.hikari.pool.HikariPool.throwPoolInitializationException(HikariPool.java:596) ~[HikariCP-5.1.0.jar:na]
Caused by: org.postgresql.util.PSQLException: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
\tat org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:342) ~[postgresql-42.7.4.jar:42.7.4]`
  },
  {
    id: 'npe',
    name: 'NullPointerException',
    technology: 'JAVA',
    description: 'Cannot invoke method because return value of getter is null',
    errorText: `java.lang.NullPointerException: Cannot invoke "com.example.model.Address.getZipCode()" because the return value of "com.example.model.User.getAddress()" is null
\tat com.example.service.ShippingCalculator.calculateTax(ShippingCalculator.java:42) ~[classes/:na]
\tat com.example.service.CheckoutService.processCheckout(CheckoutService.java:89) ~[classes/:na]
\tat com.example.controller.OrderController.submitOrder(OrderController.java:56) ~[classes/:na]`
  },
  {
    id: 'security-403',
    name: 'Spring Security 403 Access Denied',
    technology: 'SPRING_BOOT',
    description: 'Access Denied: User has authorities [ROLE_USER] but requires [ROLE_ADMIN]',
    errorText: `org.springframework.security.access.AccessDeniedException: Access Denied: User has authorities [ROLE_USER] but requires [ROLE_ADMIN]
\tat org.springframework.security.access.intercept.AbstractSecurityInterceptor.beforeInvocation(AbstractSecurityInterceptor.java:208) ~[spring-security-core-6.3.3.jar:6.3.3]
\tat org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor.invoke(MethodSecurityInterceptor.java:58) ~[spring-security-core-6.3.3.jar:6.3.3]
\tat com.example.controller.AdminUserController.deleteUser(AdminUserController.java:78) ~[classes/:na]`
  },
  {
    id: 'feign-timeout',
    name: 'Feign Client Read Timeout',
    technology: 'SPRING_BOOT',
    description: 'Read timed out executing GET http://inventory-service/api/v1/stock/1001',
    errorText: `feign.RetryableException: Read timed out executing GET http://inventory-service/api/v1/stock/1001
\tat feign.FeignException.errorExecuting(FeignException.java:268) ~[feign-core-13.3.jar:na]
\tat feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:131) ~[feign-core-13.3.jar:na]
Caused by: java.net.SocketTimeoutException: Read timed out
\tat java.base/sun.nio.ch.NioSocketImpl.timedRead(NioSocketImpl.java:278) ~[na:na]`
  },
  {
    id: 'data-integrity',
    name: 'DataIntegrityViolationException',
    technology: 'HIBERNATE',
    description: 'duplicate key value violates unique constraint "uk_users_email"',
    errorText: `org.springframework.dao.DataIntegrityViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint "uk_users_email"
  Detail: Key (email)=(alex.developer@company.com) already exists.] [insert into users (email,first_name,last_name,password_hash,plan) values (?,?,?,?,?)]
\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:290) ~[spring-orm-6.1.12.jar:6.1.12]
Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint "uk_users_email"]`
  }
];

export const TECHNOLOGIES = [
  { id: 'SPRING_BOOT', label: 'Spring Boot' },
  { id: 'JAVA', label: 'Java Core' },
  { id: 'HIBERNATE', label: 'Hibernate / JPA' },
  { id: 'POSTGRESQL', label: 'PostgreSQL' },
  { id: 'MYSQL', label: 'MySQL' },
  { id: 'DOCKER', label: 'Docker' },
  { id: 'KUBERNETES', label: 'Kubernetes' },
  { id: 'AZURE', label: 'Azure Cloud' },
  { id: 'MAVEN', label: 'Maven' },
  { id: 'GRADLE', label: 'Gradle' }
];

export const FAQS = [
  {
    q: "Does DevTools AI store my stack traces?",
    a: "No, not by default. We follow a strict privacy-first policy. Stack traces are scrubbed in memory for API keys, tokens, and passwords before analysis. Authenticated users can optionally tick 'Save this stack trace in my private history' to preserve it in their account."
  },
  {
    q: "Which Java frameworks are supported?",
    a: "DevTools AI has deep specialized knowledge for Java 8 through Java 24, Spring Boot 2.x and 3.x, Spring Data JPA, Hibernate, Spring Security, HikariCP, Feign/WebClient, PostgreSQL, MySQL, and Docker/Kubernetes container runtimes."
  },
  {
    q: "Can I use it for production errors?",
    a: "Yes! Our automatic client-side and server-side secret sanitization automatically redacts Bearer JWT tokens, database connection passwords, API keys, and credential headers before sending to the model."
  },
  {
    q: "Does it send my code to third-party AI models?",
    a: "Only the sanitized error snippet and stack trace you paste into the analyzer are sent to the AI analysis engine via our secure backend. Your raw source repository is never accessed or uploaded."
  },
  {
    q: "How accurate is the diagnosis?",
    a: "Unlike generic chat assistants, DevTools AI operates under a specialized senior debugging system prompt that requires distinguishing confirmed stack facts from speculative causes, citing exact root cause lines and providing copy-ready Spring/Java code solutions."
  }
];

export const PRICING_PLANS = [
  {
    id: 'FREE',
    name: 'Free',
    price: '$0',
    period: 'forever',
    description: 'Perfect for quick debugging during individual development.',
    limitText: '10 analyses / day',
    features: [
      '10 error analyses per day',
      'Intelligent root cause diagnosis',
      'Automatic secret redaction',
      'Copy-ready code fixes',
      'Basic error history (last 5)'
    ],
    cta: 'Start Free',
    badge: null,
    highlight: false
  },
  {
    id: 'PRO',
    name: 'Pro',
    price: '$9',
    period: 'per month',
    description: 'For professional engineers debugging complex microservices.',
    limitText: '250 analyses / day',
    features: [
      '250 analyses per day',
      'Priority high-speed AI analysis engine',
      'Unlimited analysis history',
      'Large stack traces (up to 64KB)',
      'Export diagnosis to Markdown & PDF',
      'Faster response latencies'
    ],
    cta: 'Upgrade to Pro',
    badge: 'Most Popular',
    highlight: true
  },
  {
    id: 'TEAM',
    name: 'Team',
    price: '$29',
    period: 'per month',
    description: 'Collaborative debugging and shared runbooks for dev teams.',
    limitText: 'Unlimited analyses',
    features: [
      'Multiple users & shared workspace',
      'Shared team error history & runbooks',
      'Centralized team analytics dashboard',
      'DevTools AI CLI & CI/CD webhook access',
      'Role-based access control',
      'Dedicated support channel'
    ],
    cta: 'Start Team Trial',
    badge: 'Enterprise Ready',
    highlight: false
  }
];
