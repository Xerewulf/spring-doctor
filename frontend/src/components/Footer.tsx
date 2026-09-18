import React from 'react';
import { Link } from 'react-router-dom';
import { Terminal, Shield, Cpu, Lock } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-zinc-900 bg-[#06070a] text-zinc-400 text-sm mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="md:col-span-1 space-y-3">
            <div className="flex items-center gap-2">
              <div className="w-6 h-6 rounded bg-indigo-600/20 border border-indigo-500/40 flex items-center justify-center text-indigo-400">
                <Terminal className="w-3.5 h-3.5" />
              </div>
              <span className="font-mono font-bold text-white tracking-tight">DEVTOOLS<span className="text-indigo-400">AI</span></span>
            </div>
            <p className="text-xs text-zinc-500 leading-relaxed">
              Paste your Java/Spring error. Understand the cause. Get the fix.
              Zero boilerplate, privacy-first developer intelligence.
            </p>
            <div className="flex items-center gap-2 text-[11px] text-zinc-500 pt-1">
              <Lock className="w-3.5 h-3.5 text-emerald-400" />
              <span>Automatic credential sanitization</span>
            </div>
          </div>

          {/* Tools & Doctors */}
          <div>
            <h4 className="text-xs font-semibold text-zinc-300 uppercase tracking-wider mb-3">Diagnostic Tools</h4>
            <ul className="space-y-2 text-xs">
              <li>
                <Link to="/error-doctor" className="hover:text-indigo-400 transition-colors">Spring & Java Error Doctor</Link>
              </li>
              <li>
                <Link to="/spring-boot/lazyinitializationexception" className="hover:text-indigo-400 transition-colors">Hibernate LazyInit Doctor</Link>
              </li>
              <li>
                <Link to="/spring-boot/bean-creation-exception" className="hover:text-indigo-400 transition-colors">BeanCreation Doctor</Link>
              </li>
              <li>
                <Link to="/spring-boot/feign-exception" className="hover:text-indigo-400 transition-colors">Feign Client Doctor</Link>
              </li>
            </ul>
          </div>

          {/* Supported Technologies */}
          <div>
            <h4 className="text-xs font-semibold text-zinc-300 uppercase tracking-wider mb-3">Technologies</h4>
            <ul className="space-y-2 text-xs">
              <li className="text-zinc-500">Java 8, 17, 21, 24</li>
              <li className="text-zinc-500">Spring Boot 2.x & 3.x</li>
              <li className="text-zinc-500">Hibernate & Spring Data JPA</li>
              <li className="text-zinc-500">PostgreSQL, MySQL, HikariCP</li>
              <li className="text-zinc-500">Docker, Kubernetes, Azure</li>
            </ul>
          </div>

          {/* Privacy & Trust */}
          <div>
            <h4 className="text-xs font-semibold text-zinc-300 uppercase tracking-wider mb-3">Privacy & Trust</h4>
            <p className="text-xs text-zinc-500 leading-relaxed mb-3">
              We never store stack traces by default. Secrets like bearer tokens, JWTs, and DB passwords are redacted before reaching the AI.
            </p>
            <div className="flex items-center gap-3 text-xs text-zinc-400">
              <Link to="/pricing" className="hover:text-white transition-colors">Pricing</Link>
              <span>•</span>
              <span className="text-zinc-500">SOC2 Prepared</span>
            </div>
          </div>
        </div>

        <div className="border-t border-zinc-900/80 mt-10 pt-6 flex flex-col sm:flex-row items-center justify-between text-xs text-zinc-600">
          <p>© {new Date().getFullYear()} DevTools AI. Built for Java & Spring developers.</p>
          <div className="flex items-center gap-4 mt-2 sm:mt-0">
            <span>Privacy-First AI Engine</span>
            <span>•</span>
            <span>Latency SLA &lt; 3s</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
