import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { billingApi } from '../api/endpoints';
import { PRICING_PLANS } from '../constants';
import { useAuth } from '../context/AuthContext';
import { CheckCircle2, Sparkles, Shield, AlertCircle, Loader2 } from 'lucide-react';

export const PricingPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user, isAuthenticated, refreshUser } = useAuth();

  const [currentSubscription, setCurrentSubscription] = useState<any>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  useEffect(() => {
    if (isAuthenticated) {
      billingApi.getSubscription()
        .then(setCurrentSubscription)
        .catch(console.error);
    }
  }, [isAuthenticated]);

  const handleSelectPlan = async (planId: string) => {
    if (!isAuthenticated) {
      navigate(`/login?redirect=/pricing?plan=${planId}`);
      return;
    }

    if (planId === user?.plan) {
      setActionMessage(`You are already subscribed to the ${planId} plan.`);
      return;
    }

    setIsProcessing(true);
    setActionMessage(null);

    try {
      // Calls the Stripe-ready BillingService abstraction
      const res = await billingApi.upgradePlan(planId);
      setActionMessage(`Successfully updated your subscription to ${planId}!`);
      await refreshUser();
      const sub = await billingApi.getSubscription();
      setCurrentSubscription(sub);
    } catch (err: any) {
      setActionMessage(err.message || 'Failed to update subscription.');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-12">
      <div className="text-center max-w-3xl mx-auto space-y-3">
        <div className="text-xs uppercase font-mono text-indigo-400 tracking-widest">Subscription Plans</div>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
          Simple, Predictable Plans for Developers & Teams
        </h1>
        <p className="text-sm text-zinc-400 leading-relaxed">
          Debug errors without friction. All plans include specialized AI diagnostics and automated secret scrubbing.
        </p>
      </div>

      {actionMessage && (
        <div className="max-w-xl mx-auto p-4 rounded-xl bg-indigo-500/10 border border-indigo-500/30 text-indigo-300 text-xs flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-indigo-400" />
            <span>{actionMessage}</span>
          </div>
        </div>
      )}

      {/* Pricing Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {PRICING_PLANS.map(plan => {
          const isCurrent = user?.plan === plan.id;

          return (
            <div
              key={plan.id}
              className={`p-8 rounded-2xl flex flex-col justify-between transition-all ${
                plan.highlight
                  ? 'bg-[#141724] border-2 border-indigo-500 shadow-2xl shadow-indigo-600/20 relative'
                  : 'bg-[#11131a] border border-zinc-800'
              }`}
            >
              {plan.badge && (
                <div className="absolute -top-3.5 right-6 px-3 py-1 rounded-full text-[10px] font-bold bg-indigo-600 text-white uppercase tracking-wider">
                  {plan.badge}
                </div>
              )}

              <div>
                <div className="flex items-baseline justify-between mb-2">
                  <h3 className="text-xl font-bold text-white">{plan.name}</h3>
                  <span className="text-xs font-mono text-indigo-400 font-semibold">{plan.limitText}</span>
                </div>
                <p className="text-xs text-zinc-400 min-h-[36px]">{plan.description}</p>

                <div className="flex items-baseline gap-1 my-6">
                  <span className="text-4xl font-extrabold text-white">{plan.price}</span>
                  <span className="text-xs text-zinc-500">/{plan.period}</span>
                </div>

                <ul className="space-y-3 my-6 text-xs text-zinc-300">
                  {plan.features.map((feat, i) => (
                    <li key={i} className="flex items-center gap-2.5">
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
                      <span>{feat}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <button
                type="button"
                onClick={() => handleSelectPlan(plan.id)}
                disabled={isProcessing || isCurrent}
                className={`w-full py-3 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                  isCurrent
                    ? 'bg-zinc-800 text-zinc-400 cursor-default'
                    : plan.highlight
                    ? 'bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/25'
                    : 'bg-zinc-800 hover:bg-zinc-700 text-zinc-100'
                }`}
              >
                {isCurrent ? 'Current Active Plan' : plan.cta}
              </button>
            </div>
          );
        })}
      </div>

      {/* Trust & Architecture note */}
      <div className="max-w-3xl mx-auto p-5 rounded-xl bg-zinc-900/40 border border-zinc-800 text-xs text-zinc-400 text-center space-y-1">
        <p className="font-semibold text-zinc-200">Billing Service Architecture</p>
        <p className="text-zinc-500">
          In dev/preview mode, plan transitions are simulated via the backend BillingService. In production, requests dispatch through secure Stripe Checkout sessions.
        </p>
      </div>
    </div>
  );
};
