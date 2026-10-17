
import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  OnInit,
  inject,
  PLATFORM_ID
} from '@angular/core';
import { finalize, timeout } from 'rxjs';
import { CibilApi, CibilResult } from '../../services/cibil-api';
import { CustomerSidebar } from '../../../../core/theme/customer-sidebar/customer-sidebar';

@Component({
  selector: 'app-cibil-score',
  standalone: true,
  imports: [CommonModule, CustomerSidebar],
  templateUrl: './cibil-score.html',
  styleUrl: './cibil-score.css'
})
export class CibilScore implements OnInit {
  private readonly api = inject(CibilApi);
  private readonly platformId = inject(PLATFORM_ID);

  customerId: string | null = null;
  result: CibilResult | null = null;

  loading = false;
  calculating = false;
  errorMessage = '';
  noticeMessage = '';

  // Development customer in your existing backend data.
  private readonly developmentCustomerId = '1';

  // Keep these available while the CIBIL request is pending.
  private fallbackName = 'Neha Kulkarni';
  private fallbackPan = 'ABCDE****F'; // Demo masked PAN; actual PAN from API/storage takes priority.

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.customerId =
      this.resolveCustomerId() ?? this.developmentCustomerId;

    this.loadCustomerDetails();
    this.loadLatest();
  }

  private loadCustomerDetails(): void {
    const storedName =
      localStorage.getItem('customerName') ??
      localStorage.getItem('fullName');

    if (storedName) {
      this.fallbackName = storedName;
    }

    const storedPan =
      localStorage.getItem('panNo') ??
      localStorage.getItem('panNumber') ??
      localStorage.getItem('pan');

    if (storedPan) {
      this.fallbackPan = this.maskPan(storedPan);
    }
  }

  loadLatest(): void {
    if (!this.customerId || this.loading) {
      return;
    }

    // Don't hide existing data or show a blocking loader on refresh.
    this.loading = !this.result;
    this.errorMessage = '';

    this.api.getLatest(this.customerId)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (response: any) => {
          const data = response?.data ?? response;

          if (data && typeof data === 'object') {
            // Update details independently of whether a score exists.
            this.result = {
              ...(this.result ?? {}),
              ...data
            } as CibilResult;

            if (
              data.cibilScore == null &&
              data.score == null
            ) {
              this.noticeMessage =
                'No CIBIL score found yet. Select Check Score to calculate it.';
            } else {
              this.noticeMessage = '';
            }
          } else {
            this.noticeMessage =
              'No CIBIL score found yet. Select Check Score to calculate it.';
          }
        },
        error: (err: any) => {
          if (err?.status === 404) {
            this.noticeMessage =
              'No CIBIL score found yet. Select Check Score to calculate it.';
          } else {
            this.errorMessage =
              err?.name === 'TimeoutError'
                ? 'The request timed out. Please try again.'
                : 'Unable to fetch the latest score. You can still try Check Score.';
          }
        }
      });
  }

  checkScore(): void {
    if (!this.customerId || this.calculating) {
      return;
    }

    this.calculating = true;
    this.errorMessage = '';
    this.noticeMessage = '';

    this.api.calculate(this.customerId)
      .pipe(
        timeout(15000),
        finalize(() => {
          this.calculating = false;
        })
      )
      .subscribe({
        next: (response: any) => {
          const data = response?.data ?? response;

          if (
            data &&
            typeof data === 'object' &&
            (data.cibilScore != null || data.score != null)
          ) {
            this.result = {
              ...(this.result ?? {}),
              ...data
            } as CibilResult;

            this.noticeMessage =
              'Your CIBIL score has been updated successfully.';
          } else {
            this.errorMessage =
              'The backend returned no CIBIL score. Check the API response.';
          }
        },
        error: (err: any) => {
          this.errorMessage =
            err?.status === 0
              ? 'Cannot connect to the backend. Check that Spring Boot is running.'
              : err?.name === 'TimeoutError'
                ? 'The request timed out. Please try again.'
                : 'Unable to calculate your CIBIL score. Please try again.';
        }
      });
  }

  get customerName(): string {
    const name =
      this.result?.customerName ??
      this.result?.fullName ??
      this.result?.name;

    return name ? String(name) : this.fallbackName;
  }

  get maskedPan(): string {
    const pan = String(
      (this.result as any)?.panNo ??
      (this.result as any)?.panNumber ??
      this.fallbackPan
    );

    return pan === '—' ? pan : this.maskPan(pan);
  }

  private maskPan(value: string): string {
    const pan = value.trim().toUpperCase();

    if (!pan || pan === '—') {
      return '—';
    }

    if (pan.includes('*')) {
      return pan;
    }

    if (pan.length < 6) {
      return pan;
    }

    return `${pan.slice(0, 5)}****${pan.slice(-1)}`;
  }

  get score(): number | null {
    const value =
      this.result?.cibilScore ??
      this.result?.score;

    return value != null && Number.isFinite(Number(value))
      ? Number(value)
      : null;
  }

  get scoreAngle(): number {
    const score = Math.min(900, Math.max(500, this.score ?? 700));
    return 180 - ((score - 500) / 400) * 180;
  }

  get scoreLabel(): string {
    const score = this.score;

    if (score === null) return 'Not checked';
    if (score >= 750) return 'Excellent';
    if (score >= 700) return 'Good';
    if (score >= 650) return 'Fair';

    return 'Needs improvement';
  }

  get scoreColor(): string {
    const score = this.score ?? 500;

    if (score >= 750) return '#169b62';
    if (score >= 700) return '#73ad43';
    if (score >= 650) return '#e8b33e';

    return '#e15b4f';
  }

  get eligibilityLabel(): string {
    return String(
      (this.result as any)?.eligibility ?? '—'
    ).replaceAll('_', ' ');
  }

  get eligibleAmount(): number | null {
    const amount =
      (this.result as any)?.eligibleLoanAmount ??
      (this.result as any)?.loanAmount;

    return amount != null && Number.isFinite(Number(amount))
      ? Number(amount)
      : null;
  }

  get decision(): string {
    return String(
      (this.result as any)?.decision ?? '—'
    ).replaceAll('_', ' ');
  }

  get lastChecked(): string | null {
    const value =
      (this.result as any)?.checkDate ??
      (this.result as any)?.calculatedAt ??
      (this.result as any)?.lastCheckedDate;

    return value ? String(value) : null;
  }

  private resolveCustomerId(): string | null {
    const keys = [
      'customerId',
      'customer_id',
      'loggedInCustomerId',
      'currentCustomerId'
    ];

    for (const key of keys) {
      const value = localStorage.getItem(key);

      if (value && /^\d+$/.test(value.trim())) {
        return value.trim();
      }
    }

    for (const key of ['currentUser', 'user', 'customer']) {
      try {
        const raw = localStorage.getItem(key);

        if (!raw) continue;

        const user = JSON.parse(raw);
        const value =
          user?.customerId ??
          user?.data?.customerId ??
          user?.user?.customerId;

        if (value != null && /^\d+$/.test(String(value))) {
          return String(value);
        }
      } catch {
        // Ignore invalid JSON.
      }
    }

    return null;
  }
}
