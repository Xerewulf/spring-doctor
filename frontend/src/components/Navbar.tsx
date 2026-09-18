import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Terminal, Shield, LogOut, User, Sparkles, Activity } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { usageApi } from '../api/endpoints';
import { UsageSummary } from '../types';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const location = useLocation();
  const [usage, setUsage] = useState<UsageSummary | null>(null);

  useEffect(() => {
    usageApi.getUsage()
      .then(setUsage)
      .catch(() => {});
  }, [location.pathname]);

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="sticky top-0 z-40 w-full border-b border-zinc-800/80 bg-[#090a0f]/85 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center gap-6">
          <Link to="/" className="flex items-center gap-2.5 group">
            <div className="w-8 h-8 rounded-lg bg-indigo-600/20 border border-indigo-500/40 flex items-center justify-center text-indigo-400 group-hover:border-indigo-400 transition-colors">
              <Terminal className="w-4 h-4" />
            </div>
            <div className="flex flex-col">
              <span className="font-mono font-bold tracking-tight text-white flex items-center gap-1.5 text-base">
                DEVTOOLS<span className="text-indigo-400">AI</span>
              </span>
            </div>
          </Link>

          {/* Navigation Links */}
          <nav className="hidden md:flex items-center gap-1">
            <Link
              to="/error-doctor"
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                isActive('/error-doctor')
                  ? 'bg-zinc-800 text-white'
                  : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
              }`}
            >
              Error Doctor
            </Link>
            {isAuthenticated && (
              <>
                <Link
                  to="/dashboard"
                  className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                    isActive('/dashboard')
                      ? 'bg-zinc-800 text-white'
                      : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
                  }`}
                >
                  Dashboard
                </Link>
                <Link
                  to="/history"
                  className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                    isActive('/history')
                      ? 'bg-zinc-800 text-white'
                      : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
                  }`}
                >
                  History
                </Link>
              </>
            )}
            <Link
              to="/pricing"
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                isActive('/pricing')
                  ? 'bg-zinc-800 text-white'
                  : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
              }`}
            >
              Pricing
            </Link>
          </nav>
        </div>

        {/* Right side controls */}
        <div className="flex items-center gap-3">
          {/* Usage counter badge */}
          {usage && (
            <div className="hidden sm:flex items-center gap-2 px-2.5 py-1 rounded-full text-xs bg-zinc-900 border border-zinc-800 text-zinc-300">
              <Activity className="w-3.5 h-3.5 text-indigo-400" />
              <span>
                Today: <strong className="text-white">{usage.usedToday}</strong> / {usage.dailyLimit}
              </span>
            </div>
          )}

          {isAuthenticated && user ? (
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2 px-3 py-1.5 rounded-md bg-zinc-900 border border-zinc-800 text-xs text-zinc-300">
                <User className="w-3.5 h-3.5 text-indigo-400" />
                <span className="font-mono text-zinc-200">{user.email}</span>
                <span className="px-1.5 py-0.5 rounded bg-indigo-500/20 text-indigo-300 text-[10px] font-semibold uppercase">
                  {user.plan}
                </span>
              </div>
              <button
                onClick={logout}
                className="p-1.5 rounded-md text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 transition-colors"
                title="Log out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link
                to="/login"
                className="px-3 py-1.5 rounded-md text-sm font-medium text-zinc-300 hover:text-white hover:bg-zinc-900 transition-colors"
              >
                Sign In
              </Link>
              <Link
                to="/register"
                className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-md text-sm font-medium bg-indigo-600 hover:bg-indigo-500 text-white shadow-sm transition-colors"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>Get Started</span>
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
