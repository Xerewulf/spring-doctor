import React, { useState } from 'react';
import { Diagnosis } from '../types';
import { SeverityBadge } from './SeverityBadge';
import { ConfidenceBadge } from './ConfidenceBadge';
import { CodeBlockWithCopy } from './CodeBlockWithCopy';
import {
  ShieldCheck,
  CheckSquare,
  Square,
  AlertTriangle,
  Lightbulb,
  ArrowRight,
  Tag,
  Share2,
  Check,
  RotateCcw,
  Sparkles,
  FileCheck2
} from 'lucide-react';

interface DiagnosisViewProps {
  diagnosis: Diagnosis;
  onReset?: () => void;
}

export const DiagnosisView: React.FC<DiagnosisViewProps> = ({ diagnosis, onReset }) => {
  const [checkedItems, setCheckedItems] = useState<Record<number, boolean>>({});
  const [copiedShare, setCopiedShare] = useState(false);
  const [activeFixIndex, setActiveFixIndex] = useState(0);

  const toggleCheck = (idx: number) => {
    setCheckedItems(prev => ({ ...prev, [idx]: !prev[idx] }));
  };

  const copyFullReport = async () => {
    const report = `# DevTools AI Diagnosis: ${diagnosis.detectedException || 'Java Error'}
Severity: ${diagnosis.severity} | Confidence: ${diagnosis.confidence}

## Executive Summary
${diagnosis.summary}

## Root Cause
${diagnosis.rootCause}

## Why This Happens
${diagnosis.whyItHappens}

## Suggested Fixes
${diagnosis.suggestedFixes.map(f => `### ${f.title}\n${f.description}\n\`\`\`${f.language}\n${f.code}\n\`\`\``).join('\n\n')}

## Things to Check
${diagnosis.thingsToCheck.map(t => `- [ ] ${t}`).join('\n')}
`;
    try {
      await navigator.clipboard.writeText(report);
      setCopiedShare(true);
      setTimeout(() => setCopiedShare(false), 2000);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Top Banner Header */}
      <div className="p-6 rounded-xl bg-[#11131b] border border-zinc-800 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-80 h-80 bg-indigo-500/5 rounded-full blur-3xl pointer-events-none" />

        {/* Sanitization Notice */}
        {diagnosis.sanitizationNotice && (
          <div className="mb-4 flex items-center gap-2 p-3 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-xs text-emerald-300">
            <ShieldCheck className="w-4 h-4 text-emerald-400 flex-shrink-0" />
            <span>{diagnosis.sanitizationNotice}</span>
          </div>
        )}

        <div className="flex flex-wrap items-center justify-between gap-4 pb-4 border-b border-zinc-800/80">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-indigo-600/20 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <div className="text-xs uppercase tracking-wider text-zinc-400 font-mono">Diagnosis Output</div>
              <h2 className="text-xl font-bold text-white tracking-tight">
                {diagnosis.detectedException || 'Application Runtime Error'}
              </h2>
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            <SeverityBadge severity={diagnosis.severity} />
            <ConfidenceBadge confidence={diagnosis.confidence} />
            <button
              onClick={copyFullReport}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-zinc-800 hover:bg-zinc-700 text-zinc-200 border border-zinc-700 transition-colors"
              title="Copy entire markdown report"
            >
              {copiedShare ? (
                <>
                  <Check className="w-3.5 h-3.5 text-emerald-400" />
                  <span className="text-emerald-400">Report Copied!</span>
                </>
              ) : (
                <>
                  <Share2 className="w-3.5 h-3.5" />
                  <span>Copy Report</span>
                </>
              )}
            </button>
            {onReset && (
              <button
                onClick={onReset}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-indigo-600 hover:bg-indigo-500 text-white transition-colors"
              >
                <RotateCcw className="w-3.5 h-3.5" />
                <span>Analyze Another</span>
              </button>
            )}
          </div>
        </div>

        {/* Diagnosis Summary */}
        <div className="mt-5">
          <h3 className="text-xs font-semibold uppercase tracking-wider text-indigo-400 mb-1">Diagnosis</h3>
          <p className="text-base text-zinc-100 font-medium leading-relaxed">
            {diagnosis.summary}
          </p>
        </div>

        {/* Root Cause Box */}
        <div className="mt-4 p-4 rounded-lg bg-zinc-900/90 border border-zinc-800">
          <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-zinc-300 mb-1">
            <AlertTriangle className="w-3.5 h-3.5 text-amber-400" />
            Root Cause
          </div>
          <p className="text-sm text-zinc-300 font-mono leading-relaxed">
            {diagnosis.rootCause}
          </p>
        </div>

        {/* Related Technologies Chips */}
        {diagnosis.relatedTechnologies && diagnosis.relatedTechnologies.length > 0 && (
          <div className="mt-4 flex flex-wrap items-center gap-2">
            <span className="text-xs text-zinc-400 flex items-center gap-1">
              <Tag className="w-3 h-3" /> Stack:
            </span>
            {diagnosis.relatedTechnologies.map((tech, i) => (
              <span
                key={i}
                className="px-2 py-0.5 rounded text-[11px] font-mono bg-zinc-800 text-zinc-300 border border-zinc-700/60"
              >
                {tech}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Grid: Why it happens & Suggested fixes */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Why this happens & Possible causes & Things to check */}
        <div className="lg:col-span-5 space-y-6">
          {/* Why this happens */}
          <div className="p-5 rounded-xl bg-[#11131b] border border-zinc-800">
            <div className="flex items-center gap-2 text-sm font-semibold text-white mb-2">
              <Lightbulb className="w-4 h-4 text-indigo-400" />
              Why This Happens
            </div>
            <p className="text-xs text-zinc-300 leading-relaxed">
              {diagnosis.whyItHappens}
            </p>
          </div>

          {/* Things to check checklist */}
          {diagnosis.thingsToCheck && diagnosis.thingsToCheck.length > 0 && (
            <div className="p-5 rounded-xl bg-[#11131b] border border-zinc-800">
              <div className="flex items-center gap-2 text-sm font-semibold text-white mb-3">
                <FileCheck2 className="w-4 h-4 text-indigo-400" />
                Things To Check
              </div>
              <ul className="space-y-2.5">
                {diagnosis.thingsToCheck.map((item, idx) => {
                  const isChecked = !!checkedItems[idx];
                  return (
                    <li
                      key={idx}
                      onClick={() => toggleCheck(idx)}
                      className={`flex items-start gap-2.5 p-2 rounded-lg cursor-pointer text-xs transition-colors ${
                        isChecked
                          ? 'bg-zinc-900/50 text-zinc-500 line-through'
                          : 'hover:bg-zinc-900/80 text-zinc-300'
                      }`}
                    >
                      <button type="button" className="mt-0.5 flex-shrink-0 text-indigo-400">
                        {isChecked ? (
                          <CheckSquare className="w-4 h-4 text-emerald-400" />
                        ) : (
                          <Square className="w-4 h-4 text-zinc-500" />
                        )}
                      </button>
                      <span className="leading-snug">{item}</span>
                    </li>
                  );
                })}
              </ul>
            </div>
          )}

          {/* Possible alternative causes */}
          {diagnosis.possibleCauses && diagnosis.possibleCauses.length > 0 && (
            <div className="p-5 rounded-xl bg-[#11131b] border border-zinc-800">
              <h4 className="text-xs font-semibold uppercase tracking-wider text-zinc-400 mb-2">
                Alternative Explanations
              </h4>
              <ul className="space-y-1.5 text-xs text-zinc-400">
                {diagnosis.possibleCauses.map((cause, i) => (
                  <li key={i} className="flex items-start gap-2">
                    <span className="text-zinc-600 mt-1">•</span>
                    <span>{cause}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {/* Right Column: Suggested Fixes */}
        <div className="lg:col-span-7 space-y-4">
          <div className="p-5 rounded-xl bg-[#11131b] border border-zinc-800">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-semibold text-white flex items-center gap-2">
                <ArrowRight className="w-4 h-4 text-emerald-400" />
                Suggested Fixes ({diagnosis.suggestedFixes?.length || 0})
              </h3>

              {/* Fix selector tabs if multiple */}
              {diagnosis.suggestedFixes && diagnosis.suggestedFixes.length > 1 && (
                <div className="flex items-center gap-1.5 p-1 rounded-lg bg-zinc-900 border border-zinc-800 text-xs">
                  {diagnosis.suggestedFixes.map((_, i) => (
                    <button
                      key={i}
                      onClick={() => setActiveFixIndex(i)}
                      className={`px-2.5 py-1 rounded-md transition-colors ${
                        activeFixIndex === i
                          ? 'bg-indigo-600 text-white font-medium'
                          : 'text-zinc-400 hover:text-white'
                      }`}
                    >
                      Option {i + 1}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Current Active Fix */}
            {diagnosis.suggestedFixes && diagnosis.suggestedFixes[activeFixIndex] && (
              <div className="space-y-3">
                <div className="p-3 rounded-lg bg-zinc-900/60 border border-zinc-800/80">
                  <h4 className="text-sm font-semibold text-white mb-1">
                    {diagnosis.suggestedFixes[activeFixIndex].title}
                  </h4>
                  <p className="text-xs text-zinc-300 leading-relaxed">
                    {diagnosis.suggestedFixes[activeFixIndex].description}
                  </p>
                </div>

                {/* Code block with copy button */}
                <CodeBlockWithCopy
                  code={diagnosis.suggestedFixes[activeFixIndex].code}
                  language={diagnosis.suggestedFixes[activeFixIndex].language || 'java'}
                />
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
