# Disbursement Report Backend

This module adds the Disbursement Report to the existing Loan Management System. It reuses the existing `Disbursements` table and does not create or alter database tables.

## Endpoints

### 1. Monthly report (JSON)

`GET http://localhost:8080/api/v1/reports/disbursement?fromDate=2024-01-01&toDate=2024-12-31&page=0&size=10`

Returns monthly rows and a Total row in `ApiResponse<Page<DisbursementReportResponse>>`.

### 2. Export report (CSV)

`GET http://localhost:8080/api/v1/reports/disbursement/export?fromDate=2024-01-01&toDate=2024-12-31`

Returns `disbursement-report.csv`.

## Data source and calculation

- `Disbursements.DisbursementDate` is used to filter the inclusive date range and group records by month.
- `Disbursements.DisburseAmount` is summed for the monthly amount.
- Only rows with `Status = 'DISBURSED'` (case/outer spaces ignored) are included.
- `COUNT(DISTINCT DealId)` counts distinct deals with a qualifying disbursement in each month.
- The Total row adds the monthly loan counts and amounts, matching the month-row total style in the provided report design.
- Only months with qualifying disbursements are returned; months with no matching rows are not filled with zeroes.
- The repository uses existing data. No test data insertion is required if `Disbursements` already contains qualifying records in the selected date range.

## Rate limiting

The JSON endpoint and CSV export endpoint each have their own limit of 5 GET requests per minute per client IP, using the existing `RateLimitFilter`.

## Postman

Use GET. No request body is required. Provide `fromDate` and `toDate` in `YYYY-MM-DD` format. JSON supports `page` and `size`; CSV export does not use pagination.

## Notes

The status value is assumed to be `DISBURSED`, based on the existing project test data. Check actual values with:

```sql
SELECT DISTINCT Status FROM Disbursements;
```

If your database uses a different status for completed disbursements, update the repository condition accordingly.
