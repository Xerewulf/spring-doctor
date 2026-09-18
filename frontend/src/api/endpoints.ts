import { api } from './client';
import {
  AuthResponse,
  Diagnosis,
  ErrorAnalysisRequest,
  ExampleError,
  UsageSummary,
  AnalysisSummary,
  AnalysisDetail,
  DashboardStats,
  UserDto,
} from '../types';

export const analysisApi = {
  analyzeError: (data: ErrorAnalysisRequest): Promise<Diagnosis> =>
    api.post<Diagnosis>('/analysis/error', data),

  getExamples: (): Promise<ExampleError[]> =>
    api.get<ExampleError[]>('/examples'),
};

export const authApi = {
  register: (data: { email: string; password: string; firstName?: string; lastName?: string }): Promise<AuthResponse> =>
    api.post<AuthResponse>('/auth/register', data),

  login: (data: { email: string; password: string }): Promise<AuthResponse> =>
    api.post<AuthResponse>('/auth/login', data),

  getMe: (): Promise<UserDto> =>
    api.get<UserDto>('/auth/me'),
};

export const usageApi = {
  getUsage: (): Promise<UsageSummary> =>
    api.get<UsageSummary>('/usage'),
};

export const historyApi = {
  getHistory: (page = 0, size = 20): Promise<{ content: AnalysisSummary[]; totalPages: number; totalElements: number }> =>
    api.get<{ content: AnalysisSummary[]; totalPages: number; totalElements: number }>(`/analysis/history?page=${page}&size=${size}`),

  getDetail: (id: number): Promise<AnalysisDetail> =>
    api.get<AnalysisDetail>(`/analysis/${id}`),

  delete: (id: number): Promise<void> =>
    api.delete<void>(`/analysis/${id}`),
};

export const dashboardApi = {
  getStats: (): Promise<DashboardStats> =>
    api.get<DashboardStats>('/dashboard/stats'),
};

export const billingApi = {
  getSubscription: (): Promise<any> =>
    api.get<any>('/billing/subscription'),

  upgradePlan: (plan: string): Promise<any> =>
    api.post<any>('/billing/upgrade', { plan }),

  createCheckout: (plan: string): Promise<any> =>
    api.post<any>('/billing/checkout', { plan }),
};
