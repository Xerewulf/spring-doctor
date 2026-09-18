import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { Sparkles, Terminal, ArrowRight, CheckCircle2, Shield, AlertTriangle } from 'lucide-react';
import { CodeBlockWithCopy } from '../components/CodeBlockWithCopy';

interface ErrorSeoData {
  title: string;
  exception: string;
  framework: string;
  metaDesc: string;
  summary: string;
  rootCause: string;
  commonCauses: string[];
  fixSnippet: string;
  exampleId: string;
}

const SEO_ERROR_DIRECTORY: Record<string, ErrorSeoData> = {
  'lazyinitializationexception': {
    title: 'How to Fix Hibernate LazyInitializationException in Spring Boot',
    exception: 'org.hibernate.LazyInitializationException',
    framework: 'Hibernate / Spring Boot',
    metaDesc: 'Step-by-step guide to diagnose and fix Hibernate LazyInitializationException "could not initialize proxy - no Session" in Spring Boot applications.',
    summary: 'The LazyInitializationException is thrown when your code attempts to access a lazily-fetched Hibernate entity collection or proxy after the underlying Session (persistence context) has closed.',
    rootCause: 'Detached entity proxy dereferenced without an active Hibernate transaction or persistence session.',
    commonCauses: [
      'Accessing lazy relationships in MVC controllers or Jackson serialization outside of @Transactional service boundaries.',
      'Passing detached entities to asynchronous methods or background task executors.',
      'Missing FETCH JOIN or @EntityGraph in repository queries.'
    ],
    fixSnippet: `@EntityGraph(attributePaths = {"orders"})
@Query("SELECT c FROM Customer c WHERE c.id = :id")
Optional<Customer> findByIdWithOrders(@Param("id") Long id);`,
    exampleId: 'lazy-init'
  },
  'bean-creation-exception': {
    title: 'How to Fix Spring Boot BeanCreationException & UnsatisfiedDependencyException',
    exception: 'org.springframework.beans.factory.BeanCreationException',
    framework: 'Spring Framework / Spring Boot',
    metaDesc: 'Fix Spring Boot startup error: BeanCreationException: Error creating bean with name. Resolve unsatisfied dependency and missing beans.',
    summary: 'Spring failed to instantiate an ApplicationContext bean during initialization due to an unsatisfied autowired dependency or circular bean reference.',
    rootCause: 'No qualifying bean candidate found in Spring IoC container for the required interface or type.',
    commonCauses: [
      'Missing @Service, @Component, or @Repository annotation on target class.',
      'Target bean package is not scanned by @SpringBootApplication component scanning.',
      'Multiple bean implementations without @Primary or @Qualifier specification.'
    ],
    fixSnippet: `@Service
public class StripePaymentService implements PaymentGateway {
    // Implementation
}`,
    exampleId: 'bean-creation'
  },
  'feign-exception': {
    title: 'How to Fix Spring Cloud FeignException & SocketTimeoutException',
    exception: 'feign.RetryableException: Read timed out',
    framework: 'Spring Cloud OpenFeign',
    metaDesc: 'Resolve Spring Cloud Feign client read timed out errors and configure resilient HTTP timeouts.',
    summary: 'OpenFeign HTTP client timed out waiting for an HTTP response from downstream microservice within configured connect/read timeout duration.',
    rootCause: 'Downstream microservice response time exceeded default 2-5 second Feign read timeout.',
    commonCauses: [
      'Unconfigured or default short read-timeout on high-latency downstream endpoints.',
      'Database lock or bottleneck in the target service.',
      'Lack of circuit breaking or fallback methods.'
    ],
    fixSnippet: `# application.yml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 5000
            readTimeout: 10000
            loggerLevel: basic`,
    exampleId: 'feign-timeout'
  }
};

export const SeoErrorPage: React.FC = () => {
  const { errorSlug } = useParams<{ errorSlug: string }>();
  const data = (errorSlug && SEO_ERROR_DIRECTORY[errorSlug.toLowerCase()]) || SEO_ERROR_DIRECTORY['lazyinitializationexception'];

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-10">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-xs text-zinc-500 font-mono">
        <Link to="/" className="hover:text-zinc-300">Home</Link>
        <span>/</span>
        <span className="text-zinc-400">Spring Boot Errors</span>
        <span>/</span>
        <span className="text-indigo-400 font-semibold">{errorSlug}</span>
      </div>

      {/* Main Title */}
      <div className="space-y-3">
        <span className="px-2.5 py-1 rounded text-xs font-mono bg-indigo-500/10 border border-indigo-500/20 text-indigo-300">
          {data.framework}
        </span>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight leading-tight">
          {data.title}
        </h1>
        <p className="text-sm font-mono text-zinc-400 bg-zinc-950 p-2.5 rounded-lg border border-zinc-800">
          {data.exception}
        </p>
      </div>

      {/* CTA Banner to Error Doctor */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-indigo-950/40 via-indigo-900/20 to-zinc-900 border border-indigo-500/30 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-base font-bold text-white flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-indigo-400" />
            Have this error in your stack trace?
          </h3>
          <p className="text-xs text-zinc-400 mt-1">
            Analyze your exact stack trace with DevTools AI to pinpoint the exact line number.
          </p>
        </div>
        <Link
          to={`/error-doctor?example=${data.exampleId}`}
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-semibold bg-indigo-600 hover:bg-indigo-500 text-white shadow-md transition-all self-start sm:self-auto flex-shrink-0"
        >
          <span>Diagnose in Error Doctor</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* Explanation & Causes */}
      <div className="space-y-6 text-zinc-300 text-sm leading-relaxed">
        <section className="space-y-2">
          <h2 className="text-lg font-bold text-white">What Causes This Error?</h2>
          <p>{data.summary}</p>
        </section>

        <section className="space-y-3">
          <h2 className="text-lg font-bold text-white">Most Common Triggers</h2>
          <ul className="space-y-2 text-xs">
            {data.commonCauses.map((cause, i) => (
              <li key={i} className="flex items-start gap-2.5 p-3 rounded-lg bg-[#11131a] border border-zinc-800">
                <CheckCircle2 className="w-4 h-4 text-indigo-400 flex-shrink-0 mt-0.5" />
                <span>{cause}</span>
              </li>
            ))}
          </ul>
        </section>

        <section className="space-y-3">
          <h2 className="text-lg font-bold text-white">Recommended Code Fix</h2>
          <CodeBlockWithCopy code={data.fixSnippet} language="java" />
        </section>
      </div>
    </div>
  );
};
