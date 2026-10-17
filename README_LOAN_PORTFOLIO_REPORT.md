# Loan Portfolio Report

## Endpoints

- JSON report: `GET http://localhost:8080/api/v1/reports/loan-portfolio?asOfDate=2026-12-31&page=0&size=10`
- CSV export: `GET http://localhost:8080/api/v1/reports/loan-portfolio/export?asOfDate=2026-12-31`

The JSON endpoint returns a paginated `ApiResponse` with product rows and a Total row. The CSV endpoint returns the full report as a downloadable CSV. Both endpoints have independent 5-requests-per-minute-per-IP counters in `RateLimitFilter`.

## Calculation and limitations

- Reads existing `LoanAccounts` and `LoanDeals` tables; no report table or schema migration is added.
- Includes `LoanStatus = 'ACTIVE'` accounts with `DisbursementDate` on or before the supplied as-of date.
- Groups by `LoanDeals.LoanType`; counts active loan accounts and sums `LoanAccounts.OutstandingPrincipal`.
- Percentage is each product's outstanding amount divided by total outstanding amount.
- `OutstandingPrincipal` is the current stored balance. Filtering by `DisbursementDate` does not reconstruct historical balances as of a past date. Use a current/as-of date only when this limitation is acceptable.
- The test-data script `database/04_loan_portfolio_report_test_data.sql` inserts illustrative data and should be run only once against an existing `LoanApp` schema with at least one customer. It does not drop/recreate the database.

## Postman

Use GET for both endpoints. If Spring Security protects the API, configure the required Bearer JWT. The date format is `YYYY-MM-DD`.
