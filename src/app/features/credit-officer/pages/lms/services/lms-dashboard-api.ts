import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface LmsApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: unknown;
}

export interface LoanPortfolioItem {
  product: string;
  activeLoans: number;
  outstandingAmount: number;
  percentageOfOutstanding: number;
}

export interface MonthlyDisbursement {
  month: string;
  loansDisbursed: number;
  disbursementAmount: number;
}

export interface MonthlyCollectionEfficiency {
  year: number;
  month: number;
  monthName: string;
  amountDue: number;
  amountCollected: number;
  efficiencyPercentage: number;
}

export interface LmsDashboardData {
  totalLoans: number;
  activeLoans: number;
  closedLoans: number;
  portfolio: LoanPortfolioItem[];
  monthlyDisbursements: MonthlyDisbursement[];
  monthlyCollectionEfficiency: MonthlyCollectionEfficiency[];
}

@Injectable({ providedIn: 'root' })
export class LmsDashboardApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1/dashboard/lms';
  private cachedDashboard: LmsDashboardData | null = null;

  getCachedDashboard(): LmsDashboardData | null {
    return this.cachedDashboard;
  }

  getDashboard(fromDate: string, toDate: string): Observable<LmsApiResponse<LmsDashboardData>> {
    const params = new HttpParams().set('fromDate', fromDate).set('toDate', toDate);
    return this.http.get<LmsApiResponse<LmsDashboardData>>(this.baseUrl, { params }).pipe(
      tap(response => {
        if (response?.success && response.data) this.cachedDashboard = response.data;
      })
    );
  }
}
