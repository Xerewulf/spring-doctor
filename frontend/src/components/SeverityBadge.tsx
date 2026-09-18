import React from 'react';
import { AlertCircle, AlertTriangle, Info, ShieldAlert } from 'lucide-react';

interface SeverityBadgeProps {
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | string;
}

export const SeverityBadge: React.FC<SeverityBadgeProps> = ({ severity }) => {
  const sev = (severity || 'MEDIUM').toUpperCase();

  switch (sev) {
    case 'CRITICAL':
      return (
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-500/15 text-rose-300 border border-rose-500/30">
          <ShieldAlert className="w-3.5 h-3.5" />
          Critical Severity
        </span>
      );
    case 'HIGH':
      return (
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-500/15 text-amber-300 border border-amber-500/30">
          <AlertCircle className="w-3.5 h-3.5" />
          High Severity
        </span>
      );
    case 'MEDIUM':
      return (
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-yellow-500/15 text-yellow-300 border border-yellow-500/30">
          <AlertTriangle className="w-3.5 h-3.5" />
          Medium Severity
        </span>
      );
    case 'LOW':
    default:
      return (
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-500/15 text-emerald-300 border border-emerald-500/30">
          <Info className="w-3.5 h-3.5" />
          Low Severity
        </span>
      );
  }
};
