export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errorCode?: string;
  timestamp?: string;
}

export interface UserDto {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  plan: 'FREE' | 'PRO' | 'TEAM';
  roles: string[];
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: UserDto;
}

export interface SuggestedFix {
  title: string;
  description: string;
  code: string;
  language: string;
}

export interface Diagnosis {
  summary: string;
  rootCause: string;
  confidence: 'HIGH' | 'MEDIUM' | 'LOW';
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  whyItHappens: string;
  suggestedFixes: SuggestedFix[];
  thingsToCheck: string[];
  relatedTechnologies: string[];
  possibleCauses: string[];
  detectedException?: string;
  sanitizationNotice?: string;
  analysisId?: number;
  wasInputSaved?: boolean;
}

export interface ErrorAnalysisRequest {
  errorText: string;
  technology: string;
  context?: string;
  saveInput?: boolean;
}

export interface UsageSummary {
  plan: string;
  dailyLimit: number;
  usedToday: number;
  remainingToday: number;
  authenticated: boolean;
}

export interface AnalysisSummary {
  id: number;
  title: string;
  errorType: string;
  technology: string;
  summary: string;
  isSavedInput: boolean;
  createdAt: string;
}

export interface AnalysisDetail {
  id: number;
  title: string;
  errorType: string;
  technology: string;
  summary: string;
  rawErrorTextRedacted?: string;
  diagnosis: Diagnosis;
  isSavedInput: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  totalAnalyses: number;
  usedToday: number;
  dailyLimit: number;
  remainingToday: number;
  plan: string;
  mostCommonError: string;
  mostCommonTechnology: string;
  recentAnalyses: AnalysisSummary[];
}

export interface ExampleError {
  id: string;
  name: string;
  technology: string;
  description: string;
  errorText: string;
}
