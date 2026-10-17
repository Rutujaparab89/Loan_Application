import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LosDashboardApi, LosDashboardData, MonthlyApplicationCount } from './services/los-dashboard-api';

@Component({
  selector: 'app-los-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './los-dashboard.html',
  styleUrl: './los-dashboard.css'
})
export class LosDashboard implements OnInit {
  private readonly api = inject(LosDashboardApi);
  fromDate = `${new Date().getFullYear()}-01-01`;
  toDate = new Date().toISOString().slice(0, 10);
  loading = false;
  errorMessage = '';
  dashboard: LosDashboardData = {
    totalApplications: 0, approvedApplications: 0, rejectedApplications: 0,
    pendingApplications: 0, applicationFunnel: {}, monthlyTrend: []
  };
  readonly monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  ngOnInit(): void { this.loadDashboard(); }

  loadDashboard(): void {
    if (!this.fromDate || !this.toDate || this.fromDate > this.toDate) {
      this.errorMessage = 'Please select a valid date range.';
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.api.getDashboard(this.fromDate, this.toDate).subscribe({
      next: (response) => {
        if (response?.success && response.data) this.dashboard = response.data;
        else if (response && 'data' in response && response.data) this.dashboard = response.data as LosDashboardData;
        else this.errorMessage = typeof response?.error === 'string' ? response.error : (response?.message || 'No dashboard data returned.');
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.status === 429
          ? 'Rate limit reached. Wait a minute and try again.'
          : error.status === 0
            ? 'Cannot connect to the backend. Check that Spring Boot is running on port 8080 and CORS is configured.'
            : (error.error?.message || `Unable to load dashboard (HTTP ${error.status}).`);
      }
    });
  }

  get funnelEntries(): { label: string; value: number }[] {
    const entries = Object.entries(this.dashboard.applicationFunnel || {});
    return entries.length ? entries.map(([label, value]) => ({ label: this.prettyStatus(label), value: Number(value) || 0 })) : [
      { label: 'Applications', value: this.dashboard.totalApplications },
      { label: 'Pending', value: this.dashboard.pendingApplications },
      { label: 'Approved', value: this.dashboard.approvedApplications }
    ];
  }

  get maxFunnel(): number { return Math.max(1, ...this.funnelEntries.map(x => x.value)); }
  get trendYear(): number { return Number(this.fromDate.slice(0, 4)) || new Date().getFullYear(); }

  // Always provide Jan-Dec so the line chart has a continuous monthly axis.
  get chartMonths(): { year: number; month: number; applications: number; label: string }[] {
    const counts = new Map<number, number>();
    for (const item of this.dashboard.monthlyTrend || []) {
      if (Number(item.year) === this.trendYear) counts.set(Number(item.month), Number(item.applications) || 0);
    }
    return this.monthNames.map((label, index) => ({
      year: this.trendYear, month: index + 1, applications: counts.get(index + 1) || 0, label
    }));
  }

  get maxMonthly(): number { return Math.max(1, ...this.chartMonths.map(x => x.applications)); }

  get trendLinePoints(): string {
    return this.chartMonths.map((item, index) => {
      const x = 8 + index * (704 / 11);
      const y = 190 - (item.applications / this.maxMonthly) * 160;
      return `${x},${y}`;
    }).join(' ');
  }

  trendX(index: number): number { return 8 + index * (704 / 11); }
  trendY(item: { applications: number }): number { return 190 - (item.applications / this.maxMonthly) * 160; }
  monthLabel(item: MonthlyApplicationCount): string { return this.monthNames[item.month - 1] || String(item.month); }
  prettyStatus(value: string): string { return value.toLowerCase().replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()); }
  trackMonth = (_: number, item: MonthlyApplicationCount) => `${item.year}-${item.month}`;
}
