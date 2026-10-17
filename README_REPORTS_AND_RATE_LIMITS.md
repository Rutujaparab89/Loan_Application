# Product Wise and Score Card Wise Reports

## Product Wise Report
- JSON: `GET /api/v1/reports/product-wise?fromDate=2026-01-01&toDate=2026-12-31&page=0&size=10`
- CSV: `GET /api/v1/reports/product-wise/export?fromDate=2026-01-01&toDate=2026-12-31`

## Score Card Wise Report
- JSON: `GET /api/v1/reports/score-card-wise?fromDate=2026-01-01&toDate=2026-12-31&page=0&size=10`
- CSV: `GET /api/v1/reports/score-card-wise/export?fromDate=2026-01-01&toDate=2026-12-31`

## Rate limiting
Each of the four report endpoints has its own counter per client IP: **5 requests per 60 seconds per endpoint**. Calls to the JSON endpoint do not consume the CSV endpoint's limit, and Product Wise requests do not consume the Score Card Wise limit. Other endpoints retain their existing limits.

The Product Wise report uses existing `LoanDeals`, `DealReviews`, and `Disbursements` tables and filters by `Disbursements.DisbursementDate`; no schema migration is included. Score Card Wise uses `ScoreCards.AppliedDate`.

Configure database credentials in `src/main/resources/application.properties` for your local environment. Do not run SQL setup scripts that drop `LoanApp` against a database containing data.
