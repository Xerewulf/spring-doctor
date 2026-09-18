import React from 'react';
import { Link } from 'react-router-dom';
import {
  Terminal,
  Sparkles,
  ArrowRight,
  ShieldCheck,
  CheckCircle2,
  Cpu,
  Layers,
  Database,
  Lock,
  ChevronRight,
  Code2,
  Zap,
  HelpCircle
} from 'lucide-react';
import { FAQS, PRICING_PLANS, TECHNOLOGIES } from '../constants';
import { CodeBlockWithCopy } from '../components/CodeBlockWithCopy';

export const LandingPage: React.FC = () => {
  return (
    <div className="space-y-24 py-10">
      {/* Hero Section */}
      <section className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center pt-8 pb-12">
        {/* Background glow */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[350px] bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 mb-6">
          <Sparkles className="w-3.5 h-3.5" />
          <span>DevTools AI for Java & Spring Boot</span>
        </div>

        <h1 className="text-4xl sm:text-6xl font-extrabold text-white tracking-tight leading-[1.1] max-w-4xl mx-auto">
          Debug Java & Spring Errors <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-sky-300">Faster</span>
        </h1>

        <p className="mt-6 text-lg sm:text-xl text-zinc-400 max-w-2xl mx-auto font-normal leading-relaxed">
          Paste your stack trace. Understand the root cause. Get practical fixes.
        </p>

        {/* CTA Buttons */}
        <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link
            to="/error-doctor"
            className="flex items-center gap-2 px-7 py-3.5 rounded-xl text-sm font-semibold bg-indigo-600 hover:bg-indigo-500 text-white shadow-xl shadow-indigo-600/25 transition-all transform hover:-translate-y-0.5"
          >
            <span>Analyze an Error</span>
            <ArrowRight className="w-4 h-4" />
          </Link>

          <Link
            to="/error-doctor?example=lazy-init"
            className="flex items-center gap-2 px-6 py-3.5 rounded-xl text-sm font-semibold bg-zinc-900 hover:bg-zinc-800 text-zinc-200 border border-zinc-800 hover:border-zinc-700 transition-all"
          >
            <span>Try an Example</span>
            <Terminal className="w-4 h-4 text-zinc-400" />
          </Link>
        </div>

        {/* Quick Tech Badges */}
        <div className="mt-12 flex flex-wrap items-center justify-center gap-2 text-xs text-zinc-500">
          <span>Trusted stack:</span>
          {['Java 21', 'Spring Boot 3', 'Hibernate', 'PostgreSQL', 'Docker', 'Kubernetes'].map(t => (
            <span key={t} className="px-2.5 py-1 rounded bg-zinc-900 border border-zinc-800 text-zinc-400 font-mono">
              {t}
            </span>
          ))}
        </div>

        {/* Interactive Mockup Preview Card */}
        <div className="mt-14 max-w-5xl mx-auto rounded-2xl bg-[#11131a] border border-zinc-800 shadow-2xl overflow-hidden text-left">
          <div className="flex items-center justify-between px-4 py-3 border-b border-zinc-800/80 bg-zinc-900/60">
            <div className="flex items-center gap-2">
              <div className="w-3 h-3 rounded-full bg-rose-500/80" />
              <div className="w-3 h-3 rounded-full bg-amber-500/80" />
              <div className="w-3 h-3 rounded-full bg-emerald-500/80" />
              <span className="ml-2 font-mono text-xs text-zinc-400">ErrorDoctor — LazyInitializationException Diagnosis</span>
            </div>
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              HIGH CONFIDENCE
            </span>
          </div>

          <div className="p-6 grid grid-cols-1 md:grid-cols-12 gap-6 bg-[#0a0b10]">
            <div className="md:col-span-5 space-y-3">
              <div className="text-xs font-semibold text-rose-400 uppercase tracking-wider font-mono">
                Detected Stack Trace
              </div>
              <div className="p-3 rounded-lg bg-zinc-950 border border-zinc-800/80 font-mono text-[11px] text-zinc-400 leading-relaxed overflow-hidden">
                <p className="text-rose-300 font-semibold">org.hibernate.LazyInitializationException: could not initialize proxy - no Session</p>
                <p className="text-zinc-600 mt-1">at com.example.service.CustomerService.calculateLoyaltyTier(CustomerService.java:48)</p>
                <p className="text-zinc-600">at com.example.controller.CustomerController.getProfile(CustomerController.java:34)</p>
              </div>
              <div className="text-xs text-zinc-400">
                <strong className="text-white">Why it happens:</strong> Session closed before collection access in web serializer layer.
              </div>
            </div>

            <div className="md:col-span-7 space-y-2">
              <div className="text-xs font-semibold text-emerald-400 uppercase tracking-wider font-mono">
                Recommended Solution: @EntityGraph
              </div>
              <div className="rounded-lg bg-[#11131a] border border-zinc-800 p-3">
                <pre className="font-mono text-xs text-indigo-300 leading-relaxed overflow-x-auto">
{`@EntityGraph(attributePaths = {"orders"})
@Query("SELECT c FROM Customer c WHERE c.id = :id")
Optional<Customer> findByIdWithOrders(@Param("id") Long id);`}
                </pre>
              </div>
              <p className="text-[11px] text-zinc-400">
                Join-fetches orders eagerly within the query, preventing N+1 queries and eliminating closed Session proxies.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-14">
          <h2 className="text-xs uppercase font-semibold text-indigo-400 tracking-widest font-mono">Workflow</h2>
          <p className="mt-2 text-3xl font-bold text-white tracking-tight">How It Works</p>
          <p className="mt-2 text-zinc-400 text-sm">Four straightforward steps to eliminate debugging friction.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {[
            {
              step: '01',
              title: 'Paste Your Error',
              desc: 'Drop in any Java or Spring exception, stack trace, or server startup log.',
              icon: Terminal
            },
            {
              step: '02',
              title: 'AI Analyzes It',
              desc: 'Specialized prompt rules parse root causes and scrub confidential tokens.',
              icon: Cpu
            },
            {
              step: '03',
              title: 'Understand Cause',
              desc: 'Receive clear architectural explanation with confidence and severity ratings.',
              icon: Zap
            },
            {
              step: '04',
              title: 'Apply The Fix',
              desc: 'Copy production-ready Spring/JPA code snippets directly to your editor.',
              icon: Code2
            }
          ].map((item, idx) => (
            <div
              key={idx}
              className="p-6 rounded-2xl bg-[#11131a] border border-zinc-800/80 hover:border-indigo-500/40 transition-colors relative group"
            >
              <div className="font-mono text-xs text-indigo-400/80 font-bold mb-3">{item.step}</div>
              <div className="w-10 h-10 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400 mb-4 group-hover:scale-105 transition-transform">
                <item.icon className="w-5 h-5" />
              </div>
              <h3 className="text-base font-semibold text-white mb-1.5">{item.title}</h3>
              <p className="text-xs text-zinc-400 leading-relaxed">{item.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Supported Technologies */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 border-y border-zinc-900">
        <div className="text-center mb-8">
          <h3 className="text-xs uppercase font-semibold text-zinc-400 tracking-widest font-mono">
            Supported Technologies & Frameworks
          </h3>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-5 gap-4">
          {TECHNOLOGIES.map(tech => (
            <div
              key={tech.id}
              className="flex items-center gap-2.5 p-3.5 rounded-xl bg-zinc-900/60 border border-zinc-800 text-zinc-200 text-xs font-mono hover:border-zinc-700 transition-colors"
            >
              <div className="w-2 h-2 rounded-full bg-indigo-400" />
              <span>{tech.label}</span>
            </div>
          ))}
        </div>
      </section>

      {/* Pricing Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-14">
          <h2 className="text-xs uppercase font-semibold text-indigo-400 tracking-widest font-mono">Plans</h2>
          <p className="mt-2 text-3xl font-bold text-white tracking-tight">Predictable Developer Pricing</p>
          <p className="mt-2 text-zinc-400 text-sm">Start debugging free. Upgrade when you need deep team history.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {PRICING_PLANS.map(plan => (
            <div
              key={plan.id}
              className={`p-7 rounded-2xl flex flex-col justify-between transition-all ${
                plan.highlight
                  ? 'bg-[#151722] border-2 border-indigo-500/80 shadow-2xl shadow-indigo-600/15 relative'
                  : 'bg-[#11131a] border border-zinc-800'
              }`}
            >
              {plan.badge && (
                <div className="absolute -top-3 right-6 px-3 py-0.5 rounded-full text-[10px] font-bold bg-indigo-600 text-white uppercase tracking-wider">
                  {plan.badge}
                </div>
              )}

              <div>
                <div className="flex items-baseline justify-between mb-2">
                  <h3 className="text-lg font-bold text-white">{plan.name}</h3>
                  <span className="text-xs font-mono text-indigo-400 font-semibold">{plan.limitText}</span>
                </div>
                <p className="text-xs text-zinc-400 min-h-[32px] mb-4">{plan.description}</p>

                <div className="flex items-baseline gap-1 my-4">
                  <span className="text-3xl font-extrabold text-white">{plan.price}</span>
                  <span className="text-xs text-zinc-500">/{plan.period}</span>
                </div>

                <ul className="space-y-2.5 my-6 text-xs text-zinc-300">
                  {plan.features.map((feat, i) => (
                    <li key={i} className="flex items-center gap-2">
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
                      <span>{feat}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <Link
                to={plan.id === 'FREE' ? '/error-doctor' : `/pricing?plan=${plan.id}`}
                className={`w-full py-2.5 rounded-lg text-xs font-semibold text-center transition-colors block ${
                  plan.highlight
                    ? 'bg-indigo-600 hover:bg-indigo-500 text-white shadow-md'
                    : 'bg-zinc-800 hover:bg-zinc-700 text-zinc-200'
                }`}
              >
                {plan.cta}
              </Link>
            </div>
          ))}
        </div>
      </section>

      {/* FAQ Section */}
      <section className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-xs uppercase font-semibold text-indigo-400 tracking-widest font-mono">Questions</h2>
          <p className="mt-2 text-3xl font-bold text-white tracking-tight">Frequently Asked Questions</p>
        </div>

        <div className="space-y-4">
          {FAQS.map((faq, idx) => (
            <div key={idx} className="p-5 rounded-xl bg-[#11131a] border border-zinc-800/80">
              <h4 className="text-sm font-semibold text-white flex items-center gap-2">
                <HelpCircle className="w-4 h-4 text-indigo-400 flex-shrink-0" />
                {faq.q}
              </h4>
              <p className="mt-2 text-xs text-zinc-400 leading-relaxed pl-6">
                {faq.a}
              </p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};
