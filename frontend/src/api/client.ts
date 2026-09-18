import { ApiResponse } from '../types';

const BASE_URL = '/api/v1';

class ApiClient {
  private getToken(): string | null {
    return localStorage.getItem('devtools_token');
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const token = this.getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(options.headers as Record<string, string>),
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    const data: ApiResponse<T> = await response.json().catch(() => ({
      success: false,
      data: null as any,
      message: 'Network response was not valid JSON',
      errorCode: 'INVALID_JSON'
    }));

    if (!response.ok || !data.success) {
      const errorMsg = data.message || `Request failed with status ${response.status}`;
      const err = new Error(errorMsg) as Error & { errorCode?: string; status: number };
      err.errorCode = data.errorCode;
      err.status = response.status;
      throw err;
    }

    return data.data;
  }

  public get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' });
  }

  public post<T>(endpoint: string, body?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: body ? JSON.stringify(body) : undefined,
    });
  }

  public delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}

export const api = new ApiClient();
