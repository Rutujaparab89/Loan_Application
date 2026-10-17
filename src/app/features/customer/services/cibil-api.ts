import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface CibilResult {
  customerId?: number | string;
  customerName?: string;
  fullName?: string;
  name?: string;
  panNo?: string;
  panNumber?: string;
  cibilScore?: number;
  score?: number;
  checkDate?: string;
  calculatedAt?: string;
  lastCheckedDate?: string;
  income?: number;
  monthlyIncome?: number;
  loanAmount?: number;
  eligibleLoanAmount?: number;
  status?: string;
  decision?: string;
  eligibility?: string;
  riskCategory?: string;
  employmentType?: string;
  age?: number;
  foir?: number;
  [key: string]: unknown;
}

interface ApiEnvelope<T> {
  success?: boolean;
  message?: string;
  data?: T;
}

@Injectable({ providedIn: 'root' })
export class CibilApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1/cibil';

  getLatest(customerId: string | number): Observable<CibilResult> {
    return this.http.get<ApiEnvelope<CibilResult> | CibilResult>(`${this.baseUrl}/customer/${customerId}`)
      .pipe(map(response => this.unwrap(response)));
  }

  calculate(customerId: string | number): Observable<CibilResult> {
    return this.http.post<ApiEnvelope<CibilResult> | CibilResult>(`${this.baseUrl}/calculate/${customerId}`, {})
      .pipe(map(response => this.unwrap(response)));
  }

  private unwrap(response: ApiEnvelope<CibilResult> | CibilResult): CibilResult {
    if (response && typeof response === 'object' && 'data' in response && response.data) {
      return response.data as CibilResult;
    }
    return response as CibilResult;
  }
}
