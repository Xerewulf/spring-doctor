import React from 'react';
import { HelpCircle } from 'lucide-react';

interface ConfidenceBadgeProps {
  confidence: 'HIGH' | 'MEDIUM' | 'LOW' | string;
}

export const ConfidenceBadge: React.FC<ConfidenceBadgeProps> = ({ confidence }) => {
  const conf = (confidence || 'MEDIUM').toUpperCase();

  const getStyle = () => {
    switch (conf) {
      case 'HIGH':
        return 'bg-indigo-500/15 text-indigo-300 border-indigo-500/30';
      case 'MEDIUM':
        return 'bg-blue-500/15 text-blue-300 border-blue-500/30';
      case 'LOW':
      default:
        return 'bg-zinc-500/15 text-zinc-300 border-zinc-500/30';
    }
  };

  return (
    <div className="relative group inline-flex items-center">
      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${getStyle()}`}>
        <span>{conf} Confidence</span>
        <HelpCircle className="w-3 h-3 opacity-70 group-hover:opacity-100 cursor-help" />
      </span>

      {/* Tooltip */}
      <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 hidden group-hover:flex flex-col w-56 p-2 rounded-md bg-zinc-900 border border-zinc-700 text-[11px] text-zinc-300 shadow-xl z-50 pointer-events-none">
        <span className="font-semibold text-zinc-100 mb-0.5">Assessment Level</span>
        Represents the AI model's assessment based solely on the provided stack trace context.
      </div>
    </div>
  );
};
