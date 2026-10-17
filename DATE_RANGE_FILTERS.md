# Report Dashboard Date Filters

The combined Report Dashboard is available at `/officer/reports`. The regular Loan Officer dashboard remains at `/officer/dashboard`.

- The From Date defaults to January 1 of the current year.
- The To Date defaults to today's local date.
- Clicking Refresh validates the range and sends the same selected `fromDate` and `toDate` to both `GET /api/v1/dashboard/los` and `GET /api/v1/dashboard/lms`.
- Dates are sent as ISO dates (`YYYY-MM-DD`).
- LOS filters ScoreCards by `AppliedDate`, and disbursements by `DisbursementDate`; the monthly application trend is based on application dates.
- LMS filters loan-account totals and active portfolio by `LoanAccounts.CreatedAt`, disbursements by `Disbursements.DisbursementDate`, and collection efficiency by EMI due dates and payment dates.

The backend must be running and the database must contain records in the selected period for non-zero results. Date filters do not create missing database records.
