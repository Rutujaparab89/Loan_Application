import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LmsDashboardApi, LmsDashboardData, MonthlyCollectionEfficiency, MonthlyDisbursement } from './services/lms-dashboard-api';

@Component({
  selector: 'app-lms-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lms-dashboard.html',
  styleUrl: './lms-dashboard.css'
})
export class LmsDashboard implements OnInit {
  private readonly api = inject(LmsDashboardApi);
  fromDate = `${new Date().getFullYear()}-01-01`;
  toDate = this.localDate(new Date());
  loading = false;
  errorMessage = '';
  dashboard: LmsDashboardData = {
    totalLoans: 0,
    activeLoans: 0,
    closedLoans: 0,
    portfolio: [],
    monthlyDisbursements: [],
    monthlyCollectionEfficiency: []
  };

  ngOnInit(): void { this.loadDashboard(); }

  loadDashboard(): void {
    if (!this.fromDate || !this.toDate || this.fromDate > this.toDate) {
      this.errorMessage = 'Please select a valid date range.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.api.getDashboard(this.fromDate, this.toDate).subscribe({
      next: response => {
        if (response?.data) {
          this.dashboard = response.data;
        } else {
          this.errorMessage = typeof response?.error === 'string'
            ? response.error : (response?.message || 'No dashboard data returned.');
        }
        this.loading = false;
      },
      error: error => {
        this.loading = false;
        this.errorMessage = error.status === 429
          ? 'Rate limit reached. Wait a minute and try again.'
          : error.status === 0
            ? 'Cannot connect to the backend. Check that Spring Boot is running on port 8080 and CORS is configured.'
            : (error.error?.message || `Unable to load dashboard (HTTP ${error.status}).`);
      }
    });
  }

  get maxDisbursement(): number {
    return Math.max(1, ...this.dashboard.monthlyDisbursements.map(item => Number(item.disbursementAmount) || 0));
  }

  get maxCollection(): number {
    return Math.max(1, ...this.dashboard.monthlyCollectionEfficiency.map(item => Number(item.amountDue) || 0));
  }

  numeric(value: number | null | undefined): number { return Number(value) || 0; }

  money(value: number | null | undefined): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(value) || 0);
  }

  disbursementLabel(item: MonthlyDisbursement): string { return item.month || '—'; }
  collectionLabel(item: MonthlyCollectionEfficiency): string { return `${item.monthName || item.month} ${item.year}`; }
  trackCollection = (_: number, item: MonthlyCollectionEfficiency) => `${item.year}-${item.month}`;
  trackDisbursement = (index: number, item: MonthlyDisbursement) => `${index}-${item.month}`;

  private localDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
