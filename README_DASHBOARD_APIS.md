# LOS / LMS Dashboard APIs

These dashboard endpoints reuse existing database tables and report repositories. No NPA card or NPA count is included, per dashboard scope.

## 1. LOS Dashboard

`GET /api/v1/dashboard/los?fromDate=2026-01-01&toDate=2026-10-14`

Both dates are optional. If omitted, the endpoint uses the first day of the month 11 months before the current month through today.

Returns total, approved, rejected and pending application counts, counts grouped by stored `ScoreCards.CurrentStatus`, and monthly counts grouped by `ScoreCards.AppliedDate`.

**Note:** application counts are based on `ScoreCards`. If this table contains multiple score-card records for one application, these counts represent score-card records, not unique applications. Confirm business meaning before presenting them as unique application counts.

## 2. LMS Dashboard

`GET /api/v1/dashboard/lms?fromDate=2026-01-01&toDate=2026-10-14`

Returns total, active and closed loan counts from `LoanAccounts`, existing product-wise active portfolio data, and existing monthly disbursement report data. NPA is intentionally excluded.

Collection efficiency is not calculated yet. It should be added only after confirming business rules for scheduled EMI amounts and successful payments, including partial payments, late payments and date cutoffs.

## 3. Total Loans — View Report

`GET /api/v1/reports/loans`

Returns loan-account rows with account number, customer/deal IDs, product, loan amount, outstanding principal, status, interest rate, tenure, EMI amount, disbursement date and total paid amount. This endpoint supports the **View Report** action on the Total Loans dashboard card.

## Postman

Use method `GET` and the URLs above. Ensure the application is connected to the `LoanApp` database and the schema contains the columns used by the existing loan portfolio report (`LoanAccounts`, `LoanDeals`, `Disbursements`, and `ScoreCards`).

## LMS monthly collection efficiency

The LMS dashboard response now returns `monthlyCollectionEfficiency`, with `amountDue`, `amountCollected`, and `efficiencyPercentage` for each month.

Formula: `amountCollected / amountDue * 100`.

The current query assumes these existing MySQL columns:
- `EmiSchedules(DueDate, Emi)`
- `LoanPayments(PaymentDate, PaymentAmount, PaymentStatus)`

Payments are grouped by the month they were received (cash-collection basis); EMIs are grouped by their due month. This means a payment for an overdue EMI is counted in the month the payment is received. The query counts `PAID`, `SUCCESS`, and `COMPLETED` payment statuses. Verify these names against your actual database and adjust if your schema/status vocabulary differs. No table is created by this change.

Endpoint example:
`GET /api/v1/dashboard/lms?fromDate=2026-01-01&toDate=2026-10-14`

Note: compile was not verified in this environment because the Maven Wrapper could not download Maven. Test the endpoint against the actual schema before Angular integration.


## Simplified LOS/LMS dashboard business logic

- LOS dashboard reads application records through `ScoreCardRepository`, then calculates status totals, funnel counts, and monthly application counts in the service.
- LMS dashboard uses JPA `EntityManager` for loan counts and the collection aggregate query; the existing portfolio and disbursement report repositories are reused.
- `JdbcTemplate` has been removed from `LmsDashboardServiceImpl`. Existing report repositories were intentionally left unchanged to avoid changing report behavior.
- Collection efficiency is calculated as successful payments received in a month divided by EMI amounts due in that month. Verify the actual database column names and status values before production use.
- Dashboard controllers, `ApiResponse` format, rate limiting, and report endpoints are retained.
