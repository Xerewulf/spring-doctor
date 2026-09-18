import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api/endpoints';
import { DashboardStats } from '../types';
import { useAuth } from '../context/AuthContext';
import {
  Activity,
  BarChart3,
  Calendar,
  Clock,
  ExternalLink,
  Layers,
  Sparkles,
  Terminal,
  Zap,
  ArrowRight
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    dashboardApi.getStats()
      .then(res => {
        setStats(res);
        setIsLoading(false);
      })
      .catch(err => {
        console.error('Failed to load dashboard metrics', err);
        setIsLoading(false);
      });
  }, []);

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-16 text-center text-zinc-400">
        <Sparkles className="w-8 h-8 text-indigo-400 mx-auto animate-spin mb-3" />
        <p>Loading developer metrics...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      {/* Top Welcome Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="text-xs font-mono text-indigo-400 uppercase tracking-widest">Dashboard</div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
            Developer Overview
          </h1>
          <p className="text-xs text-zinc-400 mt-1">
            Logged in as <span className="text-zinc-200 font-mono">{user?.email}</span> (Plan: <strong className="text-indigo-400">{user?.plan}</strong>)
          </p>
        </div>

        <Link
          to="/error-doctor"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-semibold bg-indigo-600 hover:bg-indigo-500 text-white shadow-md transition-colors self-start sm:self-auto"
        >
          <Sparkles className="w-3.5 h-3.5" />
          <span>New Error Analysis</span>
        </Link>
      </div>

      {/* Metrics Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Analyses Today */}
        <div className="p-5 rounded-xl bg-[#11131a] border border-zinc-800 space-y-2">
          <div className="flex items-center justify-between text-xs text-zinc-400 font-medium">
            <span>Analyses Today</span>
            <Activity className="w-4 h-4 text-indigo-400" />
          </div>
          <div className="text-2xl font-bold text-white font-mono">
            {stats?.usedToday || 0} <span className="text-sm text-zinc-500 font-normal">/ {stats?.dailyLimit || 10}</span>
          </div>
          <div className="w-full bg-zinc-800 rounded-full h-1.5 overflow-hidden">
            <div
              className="bg-indigo-500 h-1.5 rounded-full transition-all"
              style={{ width: `${Math.min(100, ((stats?.usedToday || 0) / (stats?.dailyLimit || 10)) * 100)}%` }}
            />
          </div>
        </div>

        {/* Total Lifetime Analyses */}
        <div className="p-5 rounded-xl bg-[#11131a] border border-zinc-800 space-y-2">
          <div className="flex items-center justify-between text-xs text-zinc-400 font-medium">
            <span>Total Analyses</span>
            <BarChart3 className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-bold text-white font-mono">
            {stats?.totalAnalyses || 0}
          </div>
          <p className="text-[11px] text-zinc-500">Analyses stored in account</p>
        </div>

        {/* Most Common Error */}
        <div className="p-5 rounded-xl bg-[#11131a] border border-zinc-800 space-y-2">
          <div className="flex items-center justify-between text-xs text-zinc-400 font-medium">
            <span>Top Exception</span>
            <Zap className="w-4 h-4 text-amber-400" />
          </div>
          <div className="text-base font-bold text-white truncate font-mono" title={stats?.mostCommonError}>
            {stats?.mostCommonError || 'None'}
          </div>
          <p className="text-[11px] text-zinc-500">Most encountered issue</p>
        </div>

        {/* Most Common Technology */}
        <div className="p-5 rounded-xl bg-[#11131a] border border-zinc-800 space-y-2">
          <div className="flex items-center justify-between text-xs text-zinc-400 font-medium">
            <span>Primary Technology</span>
            <Layers className="w-4 h-4 text-sky-400" />
          </div>
          <div className="text-base font-bold text-white truncate font-mono">
            {stats?.mostCommonTechnology || 'Spring Boot'}
          </div>
          <p className="text-[11px] text-zinc-500">Active debugging framework</p>
        </div>
      </div>

      {/* Recent Analyses Section */}
      <div className="p-6 rounded-2xl bg-[#11131a] border border-zinc-800 space-y-4">
        <div className="flex items-center justify-between border-b border-zinc-800 pb-3">
          <h2 className="text-sm font-semibold text-white flex items-center gap-2">
            <Clock className="w-4 h-4 text-indigo-400" />
            Recent Analyses
          </h2>
          <Link to="/history" className="text-xs text-indigo-400 hover:text-indigo-300 flex items-center gap-1">
            <span>View All History</span>
            <ArrowRight className="w-3 h-3" />
          </Link>
        </div>

        {stats?.recentAnalyses && stats.recentAnalyses.length > 0 ? (
          <div className="divide-y divide-zinc-800/80">
            {stats.recentAnalyses.map(item => (
              <div key={item.id} className="py-3 flex items-center justify-between gap-4">
                <div className="space-y-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-white font-mono truncate">
                      {item.errorType || item.title}
                    </span>
                    <span className="px-1.5 py-0.5 rounded text-[10px] bg-zinc-800 text-zinc-400 font-mono">
                      {item.technology}
                    </span>
                  </div>
                  <p className="text-xs text-zinc-400 truncate max-w-xl">
                    {item.summary}
                  </p>
                </div>

                <div className="flex items-center gap-4 flex-shrink-0 text-xs text-zinc-500 font-mono">
                  <span>{new Date(item.createdAt).toLocaleDateString()}</span>
                  <Link
                    to={`/history?id=${item.id}`}
                    className="p-1.5 rounded bg-zinc-800 hover:bg-zinc-700 text-zinc-300 hover:text-white transition-colors"
                    title="View diagnosis"
                  >
                    <ExternalLink className="w-3.5 h-3.5" />
                  </Link>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="py-8 text-center text-xs text-zinc-500">
            No analyses recorded yet. Try running an error analysis in Error Doctor.
          </div>
        )}
      </div>
    </div>
  );
};
