# Score Card Wise Report

## Endpoints

### Search report (GET)
`http://localhost:8080/api/v1/reports/score-card-wise?fromDate=2024-01-01&toDate=2024-12-31&page=0&size=2`

### Export CSV (GET)
`http://localhost:8080/api/v1/reports/score-card-wise/export?fromDate=2024-01-01&toDate=2024-12-31`

## Rate limiting
The score-card report search and CSV export endpoints are limited to **5 GET requests per minute per client IP**. The existing global API filter is reused. The limit is shared by both report endpoints for a given IP because the filter counts requests per IP.

When the limit is exceeded, the API returns HTTP `429 Too Many Requests` with a JSON error body. Wait for the rate-limit window to reset before retrying.

## Other features
- Standard `ApiResponse` for JSON report responses.
- Pagination for report summary rows (`page` is zero-based; `size` is page size).
- CSV export includes the complete report rather than only the current paginated page.
- SLF4J logging for report requests and rate-limit events.
- Report caching is intentionally disabled so changes to `ScoreCards.CurrentStatus` are reflected immediately.
- CIBIL calculation logic is not changed by this report feature.
