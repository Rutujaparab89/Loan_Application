import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CreditOfficerSidebar } from '../../../../core/theme/credit-officer-sidebar/credit-officer-sidebar';
import { LosDashboardApi, LosDashboardData, MonthlyApplicationCount } from '../los/services/los-dashboard-api';
import { LmsDashboardApi, LmsDashboardData, MonthlyCollectionEfficiency, MonthlyDisbursement } from '../lms/services/lms-dashboard-api';

@Component({
  selector: 'app-credit-officer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, CreditOfficerSidebar],
  templateUrl: './credit-officer-dashboard.html',
  styleUrl: './credit-officer-dashboard.css'
})
export class CreditOfficerDashboard implements OnInit {
  private readonly losApi = inject(LosDashboardApi);
  private readonly lmsApi = inject(LmsDashboardApi);

  fromDate = `${new Date().getFullYear()}-01-01`;
  toDate = this.localDate(new Date());
  losLoading = false;
  lmsLoading = false;
  demoMode = false;
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

  ngOnInit(): void { this.setLosDemoData(); this.setLmsDemoData(); this.loadDashboard(); }

  get totalOutstanding(): number { return this.lmsDashboard.portfolio.reduce((sum, item) => sum + (Number(item.outstandingAmount) || 0), 0); }

  private setLosDemoData(): void {
    this.losDashboard = {
      totalApplications: 1247, approvedApplications: 425, rejectedApplications: 208, pendingApplications: 614,
      applicationFunnel: { Applications: 1247, 'In Progress': 614, 'Under Review': 425, Approved: 425, Disbursed: 310 },
      monthlyTrend: [
        {year: 2026, month: 1, applications: 62}, {year: 2026, month: 2, applications: 118},
        {year: 2026, month: 3, applications: 112}, {year: 2026, month: 4, applications: 178},
        {year: 2026, month: 5, applications: 120}, {year: 2026, month: 6, applications: 138},
        {year: 2026, month: 7, applications: 185}, {year: 2026, month: 8, applications: 201},
        {year: 2026, month: 9, applications: 172}, {year: 2026, month: 10, applications: 205},
        {year: 2026, month: 11, applications: 165}, {year: 2026, month: 12, applications: 154}
      ]
    };
  }

  private setLmsDemoData(): void {
    this.lmsDashboard = {
      totalLoans: 3562, activeLoans: 2650, closedLoans: 782,
      portfolio: [
        {product:'Home Loan', activeLoans:1260, outstandingAmount:1204000000, percentageOfOutstanding:46.93},
        {product:'Personal Loan', activeLoans:840, outstandingAmount:653000000, percentageOfOutstanding:25.46},
        {product:'Business Loan', activeLoans:310, outstandingAmount:452500000, percentageOfOutstanding:17.64},
        {product:'LAP', activeLoans:140, outstandingAmount:158000000, percentageOfOutstanding:6.16},
        {product:'Vehicle Loan', activeLoans:100, outstandingAmount:100000000, percentageOfOutstanding:3.91}
      ],
      monthlyDisbursements: [],
      monthlyCollectionEfficiency: [
        {year:2026,month:1,monthName:'Jan',amountDue:100,amountCollected:80,efficiencyPercentage:80},
        {year:2026,month:2,monthName:'Feb',amountDue:100,amountCollected:74,efficiencyPercentage:74},
        {year:2026,month:3,monthName:'Mar',amountDue:100,amountCollected:84,efficiencyPercentage:84},
        {year:2026,month:4,monthName:'Apr',amountDue:100,amountCollected:95,efficiencyPercentage:95},
        {year:2026,month:5,monthName:'May',amountDue:100,amountCollected:88,efficiencyPercentage:88},
        {year:2026,month:6,monthName:'Jun',amountDue:100,amountCollected:86,efficiencyPercentage:86},
        {year:2026,month:7,monthName:'Jul',amountDue:100,amountCollected:91,efficiencyPercentage:91},
        {year:2026,month:8,monthName:'Aug',amountDue:100,amountCollected:100,efficiencyPercentage:100},
        {year:2026,month:9,monthName:'Sep',amountDue:100,amountCollected:92,efficiencyPercentage:92},
        {year:2026,month:10,monthName:'Oct',amountDue:100,amountCollected:90,efficiencyPercentage:90},
        {year:2026,month:11,monthName:'Nov',amountDue:100,amountCollected:82,efficiencyPercentage:82},
        {year:2026,month:12,monthName:'Dec',amountDue:100,amountCollected:72,efficiencyPercentage:72}
      ]
    };
  }

  loadDashboard(): void {
    if (!this.fromDate || !this.toDate || this.fromDate > this.toDate) {
      this.losError = 'Please select a valid date range.';
      this.lmsError = 'Please select a valid date range.';
      return;
    }
    this.demoMode = false;
    this.loadLosDashboard();
    this.loadLmsDashboard();
  }

  private loadLosDashboard(): void {
    this.losLoading = true; this.losError = '';
    this.losApi.getDashboard(this.fromDate, this.toDate).subscribe({
      next: response => {
        if (response?.data) this.losDashboard = response.data;
        else this.losError = typeof response?.error === 'string' ? response.error : (response?.message || 'No LOS dashboard data returned.');
        this.losLoading = false;
      },
      error: error => { this.losLoading = false; this.losError = ''; this.demoMode = true; this.setLosDemoData(); }
    });
  }

  private loadLmsDashboard(): void {
    this.lmsLoading = true; this.lmsError = '';
    this.lmsApi.getDashboard(this.fromDate, this.toDate).subscribe({
      next: response => {
        if (response?.data) this.lmsDashboard = response.data;
        else this.lmsError = typeof response?.error === 'string' ? response.error : (response?.message || 'No LMS dashboard data returned.');
        this.lmsLoading = false;
      },
      error: error => { this.lmsLoading = false; this.lmsError = ''; this.demoMode = true; this.setLmsDemoData(); }
    });
  }

  private apiError(error: any, system: string): string {
    return error.status === 429 ? 'Rate limit reached. Wait a minute and try again.'
      : error.status === 0 ? 'Cannot connect to the backend. Check that Spring Boot is running on port 8080 and CORS is configured.'
      : (error.error?.message || `Unable to load ${system} dashboard (HTTP ${error.status}).`);
  }

  get loading(): boolean { return this.losLoading || this.lmsLoading; }
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
