import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CreditOfficerSidebar } from '../../../../core/theme/credit-officer-sidebar/credit-officer-sidebar';
import { LosDashboardApi, LosDashboardData, MonthlyApplicationCount } from '../los/services/los-dashboard-api';
import { LmsDashboardApi, LmsDashboardData, MonthlyCollectionEfficiency, MonthlyDisbursement } from '../lms/services/lms-dashboard-api';

@Component({
  selector: 'app-reports-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, CreditOfficerSidebar],
  templateUrl: './reports-dashboard.html',
  styleUrl: './reports-dashboard.css'
})
export class ReportsDashboard implements OnInit {
  private readonly losApi = inject(LosDashboardApi);
  private readonly lmsApi = inject(LmsDashboardApi);

  fromDate = `${new Date().getFullYear()}-01-01`;
  toDate = this.localDate(new Date());
  losLoading = true;
  lmsLoading = true;
  // Track whether each section already has a successful live response.
  // Background refreshes should not replace the Live data badge with Loading data.
  private losHasLiveData = false;
  private lmsHasLiveData = false;
  losError = '';
  lmsError = '';

  losDashboard: LosDashboardData = {
    totalApplications: 0, approvedApplications: 0, rejectedApplications: 0,
    pendingApplications: 0, applicationFunnel: {}, monthlyTrend: []
  };
  lmsDashboard: LmsDashboardData = {
    totalLoans: 0, activeLoans: 0, closedLoans: 0, portfolio: [],
    monthlyDisbursements: [], monthlyCollectionEfficiency: []
  };

  readonly monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  ngOnInit(): void {
    // Reuse the most recent successful live responses immediately when returning
    // from another page, then refresh both sections from the backend.
    const cachedLos = this.losApi.getCachedDashboard();
    const cachedLms = this.lmsApi.getCachedDashboard();
    if (cachedLos) { this.losDashboard = cachedLos; this.losHasLiveData = true; }
    if (cachedLms) { this.lmsDashboard = cachedLms; this.lmsHasLiveData = true; }
    this.loadDashboard();
  }

  get totalOutstanding(): number { return this.lmsDashboard.portfolio.reduce((sum, item) => sum + (Number(item.outstandingAmount) || 0), 0); }

  loadDashboard(): void {
    if (!this.fromDate || !this.toDate || this.fromDate > this.toDate) {
      this.losError = 'Please select a valid date range.';
      this.lmsError = 'Please select a valid date range.';
      return;
    }
    this.loadLosDashboard();
    this.loadLmsDashboard();
  }

  private loadLosDashboard(): void {
    this.losLoading = true; this.losError = '';
    this.losApi.getDashboard(this.fromDate, this.toDate).subscribe({
      next: response => {
        if (response?.data) { this.losDashboard = response.data; this.losHasLiveData = true; }
        else this.losError = typeof response?.error === 'string' ? response.error : (response?.message || 'No LOS dashboard data returned.');
        this.losLoading = false;
      },
      error: error => {
        this.losLoading = false;
        if (!this.losHasLiveData) this.losDashboard = { totalApplications: 0, approvedApplications: 0, rejectedApplications: 0, pendingApplications: 0, applicationFunnel: {}, monthlyTrend: [] };
        this.losError = this.apiError(error, 'LOS');
      }
    });
  }

  private loadLmsDashboard(): void {
    this.lmsLoading = true; this.lmsError = '';
    this.lmsApi.getDashboard(this.fromDate, this.toDate).subscribe({
      next: response => {
        if (response?.data) { this.lmsDashboard = response.data; this.lmsHasLiveData = true; }
        else this.lmsError = typeof response?.error === 'string' ? response.error : (response?.message || 'No LMS dashboard data returned.');
        this.lmsLoading = false;
      },
      error: error => {
        this.lmsLoading = false;
        if (!this.lmsHasLiveData) this.lmsDashboard = { totalLoans: 0, activeLoans: 0, closedLoans: 0, portfolio: [], monthlyDisbursements: [], monthlyCollectionEfficiency: [] };
        this.lmsError = this.apiError(error, 'LMS');
      }
    });
  }

  private apiError(error: any, system: string): string {
    return error.status === 429 ? 'Rate limit reached. Wait a minute and try again.'
      : error.status === 0 ? 'Cannot connect to the backend. Check that Spring Boot is running on port 8080 and CORS is configured.'
      : (error.error?.message || `Unable to load ${system} dashboard (HTTP ${error.status}).`);
  }

  // Only show Loading data before the first successful response for either section.
  // Once cached/live data exists, keep the Live data badge during background refresh.
  get loading(): boolean {
    return (this.losLoading && !this.losHasLiveData) || (this.lmsLoading && !this.lmsHasLiveData);
  }
  get funnelEntries(): { label: string; value: number }[] {
    // Prefer the status counts returned by the LOS API. Normalize common backend
    // status spellings so UNDER_REVIEW and Under Review render consistently.
    const raw = this.losDashboard.applicationFunnel || {};
    const normalized = new Map<string, number>();
    for (const [key, value] of Object.entries(raw)) {
      const status = key.toLowerCase().replace(/[ _-]/g, ' ').trim();
      normalized.set(status, Number(value) || 0);
    }
    const find = (...keys: string[]): number => {
      for (const key of keys) {
        const value = normalized.get(key.toLowerCase());
        if (value !== undefined) return value;
      }
      return 0;
    };
    const hasApiStatuses = Object.keys(raw).length > 0;
    if (hasApiStatuses) {
      return [
        { label: 'Applications', value: find('applications', 'total applications') || this.losDashboard.totalApplications },
        { label: 'In Progress', value: find('in progress', 'inprogress') },
        { label: 'Under Review', value: find('under review', 'underreview', 'in review', 'pending') },
        { label: 'Approved', value: find('approved', 'approval') },
        { label: 'Disbursed', value: find('disbursed', 'disbursed applications') }
      ];
    }
    return [
      { label: 'Applications', value: this.losDashboard.totalApplications },
      { label: 'In Progress', value: this.losDashboard.pendingApplications },
      { label: 'Under Review', value: 0 },
      { label: 'Approved', value: this.losDashboard.approvedApplications },
      { label: 'Disbursed', value: 0 }
    ];
  }
  get monthlyChartPoints(): { x: number; y: number; month: string; applications: number }[] {
    const year = Number(this.fromDate.slice(0, 4)) || new Date().getFullYear();
    const counts = new Map<number, number>();
    for (const item of this.losDashboard.monthlyTrend || []) {
      if (Number(item.year) === year) counts.set(Number(item.month), Number(item.applications) || 0);
    }
    const max = Math.max(1, ...Array.from({ length: 12 }, (_, i) => counts.get(i + 1) || 0));
    return Array.from({ length: 12 }, (_, i) => {
      const applications = counts.get(i + 1) || 0;
      return { x: 20 + i * (1160 / 11), y: 220 - (applications / max) * 200, month: this.monthNames[i], applications };
    });
  }
  get monthlyLinePoints(): string { return this.monthlyChartPoints.map(point => `${point.x},${point.y}`).join(' '); }
  trackChartPoint = (_: number, point: { x: number; month: string }) => `${point.month}-${point.x}`;
  get maxMonthly(): number { return Math.max(1, ...this.monthlyChartPoints.map(x => x.applications)); }
  get maxDisbursement(): number { return Math.max(1, ...this.lmsDashboard.monthlyDisbursements.map(x => Number(x.disbursementAmount) || 0)); }
  monthLabel(item: MonthlyApplicationCount): string { return this.monthNames[item.month - 1] || String(item.month); }
  prettyStatus(value: string): string { return value.toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()); }
  numeric(value: number | null | undefined): number { return Number(value) || 0; }
  money(value: number | null | undefined): string { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(value) || 0); }
  disbursementLabel(item: MonthlyDisbursement): string { return item.month || '—'; }
  collectionLabel(item: MonthlyCollectionEfficiency): string { return `${item.monthName || item.month} ${item.year}`; }
  trackMonth = (_: number, item: MonthlyApplicationCount) => `${item.year}-${item.month}`;
  trackCollection = (_: number, item: MonthlyCollectionEfficiency) => `${item.year}-${item.month}`;
  trackDisbursement = (index: number, item: MonthlyDisbursement) => `${index}-${item.month}`;

  private localDate(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}
