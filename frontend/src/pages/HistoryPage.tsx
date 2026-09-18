import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { historyApi } from '../api/endpoints';
import { AnalysisDetail, AnalysisSummary } from '../types';
import { DiagnosisView } from '../components/DiagnosisView';
import {
  Clock,
  Trash2,
  ExternalLink,
  Search,
  ArrowLeft,
  Calendar,
  Lock,
  Loader2,
  Sparkles
} from 'lucide-react';

export const HistoryPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [historyList, setHistoryList] = useState<AnalysisSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDetail, setSelectedDetail] = useState<AnalysisDetail | null>(null);
  const [loadingDetail, setLoadingDetail] = useState(false);

  const selectedId = searchParams.get('id');

  useEffect(() => {
    loadHistory();
  }, []);

  useEffect(() => {
    if (selectedId) {
      loadDetail(Number(selectedId));
    } else {
      setSelectedDetail(null);
    }
  }, [selectedId]);

  const loadHistory = async () => {
    try {
      setIsLoading(true);
      const res = await historyApi.getHistory(0, 50);
      setHistoryList(res.content || []);
    } catch (err) {
      console.error('Failed to load history', err);
    } finally {
      setIsLoading(false);
    }
  };

  const loadDetail = async (id: number) => {
    try {
      setLoadingDetail(true);
      const detail = await historyApi.getDetail(id);
      setSelectedDetail(detail);
    } catch (err) {
      console.error('Failed to load detail', err);
    } finally {
      setLoadingDetail(false);
    }
  };

  const handleDelete = async (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!confirm('Are you sure you want to delete this analysis?')) return;

    try {
      await historyApi.delete(id);
      setHistoryList(prev => prev.filter(item => item.id !== id));
      if (selectedDetail?.id === id) {
        setSearchParams({});
      }
    } catch (err) {
      console.error('Failed to delete analysis', err);
    }
  };

  const filtered = historyList.filter(item =>
    (item.errorType?.toLowerCase().includes(searchTerm.toLowerCase()) || '') ||
    (item.technology?.toLowerCase().includes(searchTerm.toLowerCase()) || '') ||
    (item.summary?.toLowerCase().includes(searchTerm.toLowerCase()) || '')
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      {/* Top Header */}
      <div>
        <div className="text-xs font-mono text-indigo-400 uppercase tracking-widest">Saved Diagnoses</div>
        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
          Analysis History
        </h1>
        <p className="text-xs text-zinc-400 mt-1 flex items-center gap-1.5">
          <Lock className="w-3 h-3 text-emerald-400" />
          <span>Privacy-first storage: Raw traces are omitted unless you explicitly opted in to save input.</span>
        </p>
      </div>

      {/* If an analysis detail is selected */}
      {selectedDetail ? (
        <div className="space-y-4">
          <button
            onClick={() => setSearchParams({})}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-zinc-800 hover:bg-zinc-700 text-zinc-300 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Back to History List</span>
          </button>

          {/* Render detail and diagnosis */}
          <DiagnosisView
            diagnosis={selectedDetail.diagnosis}
            onReset={() => setSearchParams({})}
          />
        </div>
      ) : loadingDetail ? (
        <div className="py-20 text-center text-zinc-400">
          <Loader2 className="w-8 h-8 text-indigo-400 mx-auto animate-spin mb-3" />
          <p className="text-xs font-mono">Loading saved diagnosis details...</p>
        </div>
      ) : (
        /* History list view */
        <div className="space-y-4">
          {/* Search bar */}
          <div className="relative max-w-md">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search by exception, technology, or message..."
              className="w-full pl-10 pr-4 py-2 rounded-xl bg-[#11131a] border border-zinc-800 text-xs text-zinc-200 placeholder:text-zinc-600 focus:outline-none focus:border-indigo-500"
            />
          </div>

          {isLoading ? (
            <div className="py-16 text-center text-zinc-400">
              <Loader2 className="w-6 h-6 text-indigo-400 mx-auto animate-spin mb-2" />
              <p className="text-xs">Loading history...</p>
            </div>
          ) : filtered.length > 0 ? (
            <div className="grid grid-cols-1 gap-3">
              {filtered.map(item => (
                <div
                  key={item.id}
                  onClick={() => setSearchParams({ id: item.id.toString() })}
                  className="p-5 rounded-xl bg-[#11131a] border border-zinc-800 hover:border-indigo-500/40 cursor-pointer transition-all flex items-center justify-between gap-4 group"
                >
                  <div className="space-y-1.5 min-w-0">
                    <div className="flex items-center gap-2.5">
                      <span className="font-mono text-sm font-semibold text-white group-hover:text-indigo-300 transition-colors truncate">
                        {item.errorType || item.title}
                      </span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-zinc-800 text-zinc-300 border border-zinc-700/60">
                        {item.technology}
                      </span>
                      {item.isSavedInput && (
                        <span className="px-1.5 py-0.5 rounded text-[10px] bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                          Saved Trace
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-zinc-400 truncate max-w-2xl">
                      {item.summary}
                    </p>
                  </div>

                  <div className="flex items-center gap-4 flex-shrink-0 text-xs font-mono text-zinc-500">
                    <span>{new Date(item.createdAt).toLocaleString()}</span>
                    <button
                      onClick={(e) => handleDelete(item.id, e)}
                      className="p-1.5 rounded text-zinc-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
                      title="Delete record"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="py-16 text-center rounded-xl bg-[#11131a] border border-zinc-800/80 text-zinc-500 space-y-3">
              <Clock className="w-8 h-8 mx-auto text-zinc-600" />
              <p className="text-xs">No analysis records match your query.</p>
              <Link
                to="/error-doctor"
                className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-semibold bg-indigo-600 hover:bg-indigo-500 text-white transition-colors"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>Analyze New Error</span>
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
