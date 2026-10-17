import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: unknown;
}

export interface MonthlyApplicationCount {
  year: number;
  month: number;
  applications: number;
}

export interface LosDashboardData {
  totalApplications: number;
  approvedApplications: number;
  rejectedApplications: number;
  pendingApplications: number;
  inProgressApplications?: number;
  underReviewApplications?: number;
  disbursedApplications?: number;
  applicationFunnel: Record<string, number>;
  monthlyTrend: MonthlyApplicationCount[];
}

@Injectable({ providedIn: 'root' })
export class LosDashboardApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1/dashboard/los';
  private cachedDashboard: LosDashboardData | null = null;

  getCachedDashboard(): LosDashboardData | null {
    return this.cachedDashboard;
  }

  getDashboard(fromDate: string, toDate: string): Observable<ApiResponse<LosDashboardData>> {
    const params = new HttpParams().set('fromDate', fromDate).set('toDate', toDate);
    return this.http.get<ApiResponse<LosDashboardData>>(this.baseUrl, { params }).pipe(
      tap(response => {
        if (response?.success && response.data) this.cachedDashboard = response.data;
      })
    );
  }
}
