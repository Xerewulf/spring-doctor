import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { analysisApi, usageApi } from '../api/endpoints';
import { Diagnosis, ExampleError, UsageSummary } from '../types';
import { DiagnosisView } from '../components/DiagnosisView';
import { EXAMPLE_ERRORS, TECHNOLOGIES } from '../constants';
import { useAuth } from '../context/AuthContext';
import {
  Sparkles,
  Trash2,
  Play,
  Shield,
  AlertCircle,
  CheckCircle2,
  ChevronDown,
  Loader2,
  FileCode,
  Info
} from 'lucide-react';

export const ErrorDoctorPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const { isAuthenticated } = useAuth();

  const [errorText, setErrorText] = useState('');
  const [technology, setTechnology] = useState('SPRING_BOOT');
  const [context, setContext] = useState('');
  const [saveInput, setSaveInput] = useState(false);

  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [diagnosis, setDiagnosis] = useState<Diagnosis | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [examplesOpen, setExamplesOpen] = useState(false);
  const [usage, setUsage] = useState<UsageSummary | null>(null);

  const MAX_CHARS = 32000;

  useEffect(() => {
    fetchUsage();

    // Check if URL query pre-fills an example or error
    const exampleParam = searchParams.get('example');
    if (exampleParam) {
      const found = EXAMPLE_ERRORS.find(e => e.id === exampleParam);
      if (found) {
        loadExample(found);
      }
    }
  }, [searchParams]);

  const fetchUsage = async () => {
    try {
      const res = await usageApi.getUsage();
      setUsage(res);
    } catch (err) {
      console.error('Failed to get usage quota', err);
    }
  };

  const loadExample = (example: ExampleError) => {
    setErrorText(example.errorText);
    setTechnology(example.technology);
    setDiagnosis(null);
    setErrorMessage(null);
    setExamplesOpen(false);
  };

  const handleClear = () => {
    setErrorText('');
    setContext('');
    setDiagnosis(null);
    setErrorMessage(null);
  };

  const handleAnalyze = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!errorText.trim()) {
      setErrorMessage('Please paste an error or stack trace before analyzing.');
      return;
    }

    setIsAnalyzing(true);
    setErrorMessage(null);

    try {
      const result = await analysisApi.analyzeError({
        errorText,
        technology,
        context: context.trim() || undefined,
        saveInput: isAuthenticated ? saveInput : false
      });
      setDiagnosis(result);
      fetchUsage();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err: any) {
      setErrorMessage(err.message || 'Failed to analyze error. Please check server logs or retry.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  // Client-side quick sensitive check indicator
  const hasPotentialSecret =
    /bearer\s+[a-z0-9_-]+\.[a-z0-9_-]+/i.test(errorText) ||
    /password\s*[:=]/i.test(errorText) ||
    /jdbc:[a-z]+:\/\/[^:]+:[^@]+@/i.test(errorText);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header section */}
      <div className="mb-8">
        <div className="flex items-center gap-2 text-xs uppercase tracking-widest text-indigo-400 font-mono mb-2">
          <span>DEVTOOLS AI</span>
          <span>•</span>
          <span>AI debugging for Java & Spring developers</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
          What's going wrong?
        </h1>
        <p className="mt-2 text-base text-zinc-400 max-w-2xl">
          Paste your Java or Spring Boot error and get a clear explanation, root cause, and suggested fix.
        </p>
      </div>

      {/* Quota Banner if near or at limit */}
      {usage && usage.remainingToday <= 0 && (
        <div className="mb-6 p-4 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-300 text-sm flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <AlertCircle className="w-5 h-5 flex-shrink-0" />
            <span>
              Daily limit reached ({usage.usedToday}/{usage.dailyLimit}).
              {!isAuthenticated && " Sign up for a free account to get 10 analyses per day, or upgrade to Pro."}
            </span>
          </div>
        </div>
      )}

      {/* Error message alert */}
      {errorMessage && (
        <div className="mb-6 p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-sm flex items-center gap-2.5">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {/* Diagnosis Output (if analyzed) */}
      {diagnosis ? (
        <DiagnosisView diagnosis={diagnosis} onReset={() => setDiagnosis(null)} />
      ) : (
        /* Input Form */
        <form onSubmit={handleAnalyze} className="space-y-6">
          <div className="p-6 rounded-2xl bg-[#11131a] border border-zinc-800 shadow-2xl space-y-4">
            {/* Top Toolbar: Tech selector & Examples dropdown */}
            <div className="flex flex-wrap items-center justify-between gap-3 pb-3 border-b border-zinc-800/80">
              <div className="flex items-center gap-2">
                <span className="text-xs font-semibold uppercase text-zinc-400">Target Tech:</span>
                <select
                  value={technology}
                  onChange={(e) => setTechnology(e.target.value)}
                  className="bg-zinc-900 border border-zinc-700 text-zinc-200 text-xs rounded-lg px-3 py-1.5 focus:outline-none focus:border-indigo-500 font-mono"
                >
                  {TECHNOLOGIES.map(t => (
                    <option key={t.id} value={t.id}>{t.label}</option>
                  ))}
                </select>
              </div>

              {/* Example picker dropdown */}
              <div className="relative">
                <button
                  type="button"
                  onClick={() => setExamplesOpen(!examplesOpen)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-zinc-800 hover:bg-zinc-700 text-zinc-200 border border-zinc-700 transition-colors"
                >
                  <FileCode className="w-3.5 h-3.5 text-indigo-400" />
                  <span>Try an Example Error</span>
                  <ChevronDown className="w-3.5 h-3.5" />
                </button>

                {examplesOpen && (
                  <div className="absolute right-0 mt-2 w-80 rounded-xl bg-[#161922] border border-zinc-700 shadow-2xl z-50 p-2 space-y-1">
                    <div className="px-2 py-1 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
                      Select realistic test error
                    </div>
                    {EXAMPLE_ERRORS.map((ex) => (
                      <button
                        key={ex.id}
                        type="button"
                        onClick={() => loadExample(ex)}
                        className="w-full text-left p-2 rounded-lg hover:bg-zinc-800/80 transition-colors text-xs text-zinc-200 flex flex-col"
                      >
                        <span className="font-semibold text-indigo-300">{ex.name}</span>
                        <span className="text-[11px] text-zinc-400 truncate">{ex.description}</span>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Stack trace textarea */}
            <div className="relative">
              <textarea
                value={errorText}
                onChange={(e) => setErrorText(e.target.value)}
                rows={14}
                maxLength={MAX_CHARS}
                placeholder="Paste your stack trace here... (e.g. org.hibernate.LazyInitializationException: could not initialize proxy - no Session...)"
                className="w-full p-4 rounded-xl bg-[#0a0b10] border border-zinc-800 text-zinc-100 font-mono text-xs leading-relaxed focus:outline-none focus:border-indigo-500/80 focus:ring-1 focus:ring-indigo-500/80 placeholder:text-zinc-600 resize-y"
              />

              {/* In-editor character count */}
              <div className="absolute bottom-3 right-3 text-[11px] font-mono text-zinc-500 bg-zinc-900/80 px-2 py-0.5 rounded border border-zinc-800">
                {errorText.length.toLocaleString()} / {MAX_CHARS.toLocaleString()}
              </div>
            </div>

            {/* Realtime secret sanitization alert */}
            {hasPotentialSecret && (
              <div className="p-3 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-xs text-indigo-300 flex items-center gap-2">
                <Shield className="w-4 h-4 text-indigo-400 flex-shrink-0" />
                <span>Detected potential credentials or tokens. DevTools AI will automatically redact them before diagnosis.</span>
              </div>
            )}

            {/* Optional context field */}
            <div>
              <label className="block text-xs font-medium text-zinc-400 mb-1">
                Additional context or what you were trying to do (optional):
              </label>
              <input
                type="text"
                value={context}
                onChange={(e) => setContext(e.target.value)}
                placeholder="e.g. Occurs when calling GET /api/v1/customers/5 from React client with Spring Boot 3.3"
                className="w-full px-3.5 py-2 rounded-lg bg-[#0a0b10] border border-zinc-800 text-zinc-200 text-xs focus:outline-none focus:border-indigo-500 placeholder:text-zinc-600"
              />
            </div>

            {/* Privacy notice & opt-in save checkbox */}
            <div className="pt-2 border-t border-zinc-800/60 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs text-zinc-500">
              <div className="flex items-center gap-2">
                <Info className="w-3.5 h-3.5 text-zinc-400 flex-shrink-0" />
                <span>Do not paste passwords, API keys, tokens, client secrets, or personal data.</span>
              </div>

              {isAuthenticated && (
                <label className="flex items-center gap-2 cursor-pointer text-zinc-300">
                  <input
                    type="checkbox"
                    checked={saveInput}
                    onChange={(e) => setSaveInput(e.target.checked)}
                    className="rounded bg-zinc-900 border-zinc-700 text-indigo-600 focus:ring-0"
                  />
                  <span>Save this stack trace in my private history</span>
                </label>
              )}
            </div>

            {/* Action buttons: Clear & Analyze */}
            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={handleClear}
                disabled={isAnalyzing || !errorText}
                className="flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-medium text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors disabled:opacity-40 cursor-pointer"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span>Clear</span>
              </button>

              <button
                type="submit"
                disabled={isAnalyzing || !errorText.trim()}
                className="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-semibold bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/20 transition-all disabled:opacity-50 cursor-pointer"
              >
                {isAnalyzing ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Diagnosing with AI...</span>
                  </>
                ) : (
                  <>
                    <Sparkles className="w-4 h-4" />
                    <span>Analyze Error</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </form>
      )}
    </div>
  );
};
