# Product Wise Report

## MVC / layered structure
- `Controller/ProductWiseReportController`: exposes the endpoints.
- `Service/ProductWiseReportService` and `ServiceImpl/ProductWiseReportServiceImpl`: contain report orchestration and calculations.
- `Repository/ProductWiseReportRepository`: contains the SQL queries for live database data.
- `DTO/ProductWiseReportResponse`: represents each report row.
- Reuse the Loan module's existing entities for `LoanDeals`, `DealReviews`, and `Disbursements` when merging. This ZIP does not add duplicate entities for tables owned by the Loan module, avoiding duplicate-class conflicts. The repository uses `JdbcTemplate` for aggregate queries; a separate JPA entity is not required for this report query.

## Tables used
- `LoanDeals`: product type (`LoanType`) and deal identifier (`DealId`).
- `DealReviews`: approval status (`Status`) linked by `DealId`.
- `Disbursements`: disbursement date, status, and deal identifier linked by `DealId`.

No table or column is added or modified. In particular, this report does not add
`AppliedDate` to `LoanDeals`, avoiding changes to the existing Loan module schema.

## Date filter limitation
The From Date and To Date filters use `Disbursements.DisbursementDate`.
Therefore, Total Apps and Approved are counted for distinct deals associated with
disbursement records in the selected disbursement date range; they are not counts
of applications originally submitted in that date range. The existing schema has
no application submission date, so that cannot be filtered accurately.

## Verify status values
The schema declares `DealReviews.Status` and `Disbursements.Status` as VARCHAR but
does not define their actual values. Verify them in MySQL:
- `SELECT DISTINCT Status FROM DealReviews;`
- `SELECT DISTINCT Status FROM Disbursements;`

The implementation currently treats `DealReviews.Status = 'APPROVED'` as approved
and `Disbursements.Status = 'DISBURSED'` as disbursed. Update those predicates if
the live values differ.

## Product types
Only `Home Loan` and `Vehicle Loan` are included.

## Endpoints (GET)
Search:
`/api/v1/reports/product-wise?fromDate=2026-01-01&toDate=2026-12-31&page=0&size=10`

CSV export:
`/api/v1/reports/product-wise/export?fromDate=2026-01-01&toDate=2026-12-31`

Search uses `ApiResponse` and pagination. CSV export returns the complete report.
The existing rate-limit filter applies five requests per minute per IP to these
product-wise report endpoints.
