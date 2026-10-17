# Loan Project - LOS and LMS Dashboards

This ZIP contains the existing Angular frontend with the LOS dashboard and an added LMS dashboard. Existing customer and credit-officer screens and routes are retained.

## Dashboard routes
- LOS: http://localhost:4200/los/dashboard
- LMS: http://localhost:4200/lms/dashboard

## Backend endpoints
- LOS: `GET http://localhost:8080/api/v1/dashboard/los?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD`
- LMS: `GET http://localhost:8080/api/v1/dashboard/lms?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD`

The dashboards expect the standard API response wrapper with `success`, `message`, `data`, and `error`. The dashboard data is read from `data`.

## Run on Windows PowerShell
Open PowerShell in the folder containing `package.json` and run:

```powershell
npm.cmd install
npx.cmd ng serve
```

Then open either dashboard URL above. Start the Spring Boot backend on port 8080 first. If the browser reports a CORS error, allow `http://localhost:4200` in the backend CORS configuration. If HTTP 429 appears, wait for the configured rate-limit window before refreshing.

## Included LMS panels
- Total, active, and closed loan summary cards
- Monthly disbursement chart
- Loan portfolio by product
- Monthly collection efficiency table
- Date range filter, refresh, loading state, and backend error messages


## Combined Loan Officer Dashboard
- Open `/officer/dashboard` to view LOS application metrics and LMS loan/collection metrics on one page.
- The shared date filter calls the existing LOS and LMS dashboard APIs independently.
- The page uses the Loan Officer sidebar; the FinSure sidebars were removed from the combined dashboard.
- Existing separate `/los/dashboard` and `/lms/dashboard` routes remain available. The root route now redirects to `/officer/dashboard`.
