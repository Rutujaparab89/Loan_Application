import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CibilApi, CibilResult } from '../../services/cibil-api';
import { CustomerSidebar } from '../../../../core/theme/customer-sidebar/customer-sidebar';

@Component({
  selector: 'app-cibil-eligibility-result',
  standalone: true,
  imports: [CommonModule, RouterLink, CustomerSidebar],
  templateUrl: './cibil-eligibility-result.html',
  styleUrl: './cibil-eligibility-result.css'
})
export class CibilEligibilityResult implements OnInit {
  private readonly api = inject(CibilApi);
  private readonly platformId = inject(PLATFORM_ID);
  result: CibilResult | null = null;
  loading = true;
  errorMessage = '';
  customerId: string | null = null;

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.customerId = this.getCustomerId();
    if (!this.customerId) {
      this.loading = false;
      this.errorMessage = 'Customer ID is not available. Sign in as a customer to view your eligibility result.';
      return;
    }
    this.loadResult();
  }

  loadResult(): void {
    if (!this.customerId) return;
    this.loading = true;
    this.api.getLatest(this.customerId).subscribe({
      next: result => { this.result = result; this.loading = false; this.errorMessage = ''; },
      error: () => { this.loading = false; this.errorMessage = 'No CIBIL eligibility result could be fetched. Check your backend or calculate your score first.'; }
    });
  }

  get name(): string {
    const n = this.result?.customerName ?? this.result?.fullName ?? this.result?.name;
    return String(n || localStorage.getItem('customerName') || 'Customer');
  }
  get pan(): string {
    const value = String(this.result?.panNo ?? this.result?.panNumber ?? '');
    return !value ? '—' : (value.includes('*') || value.length < 6 ? value : `${value.slice(0,5)}****${value.slice(-1)}`);
  }
  get income(): number | null {
    const value = this.result?.income ?? this.result?.monthlyIncome;
    return typeof value === 'number' ? value : null;
  }
  get score(): number | null {
    const value = this.result?.cibilScore ?? this.result?.score;
    return typeof value === 'number' ? value : null;
  }
  get loanAmount(): number | null {
    const value = this.result?.loanAmount ?? this.result?.eligibleLoanAmount;
    return typeof value === 'number' ? value : null;
  }
  get approved(): boolean {
    const value = String(this.result?.decision ?? this.result?.eligibility ?? this.result?.status ?? '').toUpperCase();
    return value.includes('APPROV') || value.includes('ELIGIB');
  }
  get statusText(): string {
    const value = this.result?.decision ?? this.result?.eligibility ?? this.result?.status;
    return value ? String(value).replaceAll('_', ' ') : 'Result unavailable';
  }
  private getCustomerId(): string | null {
    for (const key of ['customerId','customer_id','loggedInCustomerId','currentCustomerId']) {
      const value = localStorage.getItem(key);
      if (value && /^\d+$/.test(value.trim())) return value.trim();
    }
    for (const key of ['currentUser','user','customer']) {
      try { const obj = JSON.parse(localStorage.getItem(key) || 'null'); const id = obj?.customerId ?? obj?.data?.customerId ?? obj?.user?.customerId; if (id != null) return String(id); } catch { /* ignore */ }
    }
    return null;
  }
}
