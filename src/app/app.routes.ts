import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'los/dashboard', redirectTo: 'officer/los', pathMatch: 'full' },
  { path: 'lms/dashboard', redirectTo: 'officer/lms', pathMatch: 'full' },


  {
    path: 'customer/cibil',
    loadComponent: () => import('./features/customer/pages/cibil-score/cibil-score').then(m => m.CibilScore)
  },
  {
    path: 'customer/cibil/result',
    loadComponent: () => import('./features/customer/pages/cibil-eligibility-result/cibil-eligibility-result').then(m => m.CibilEligibilityResult)
  },

  {
    path: 'customer/dashboard',
    loadComponent: () =>
      import('./features/customer/pages/customer-dashboard/customer-dashboard')
        .then(m => m.CustomerDashboard)
  },

  // Independent LOS and LMS pages under features/credit-officer/pages/los and /lms.
  {
    path: 'officer/los',
    loadComponent: () =>
      import('./features/credit-officer/pages/los/los-dashboard')
        .then(m => m.LosDashboard)
  },
  {
    path: 'officer/lms',
    loadComponent: () =>
      import('./features/credit-officer/pages/lms/lms-dashboard')
        .then(m => m.LmsDashboard)
  },

  // Keep the regular officer dashboard separate from the combined reports page.
  {
    path: 'officer/dashboard',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard/dashboard')
        .then(m => m.Dashboard)
  },

  // Combined LOS + LMS dashboard is shown only on the Reports route.
  {
    path: 'officer/reports',
    loadComponent: () =>
      import('./features/credit-officer/pages/reports-dashboard/reports-dashboard')
        .then(m => m.ReportsDashboard)
  },

  { path: '', redirectTo: 'officer/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'officer/dashboard' }
];
