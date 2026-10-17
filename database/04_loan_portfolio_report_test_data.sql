-- ============================================================
-- LOAN PORTFOLIO REPORT - DEVELOPMENT TEST DATA
-- Uses existing LoanApp tables. Does NOT drop/recreate database
-- or create duplicate tables.
-- ============================================================

USE LoanApp;

-- 1. Verify required tables exist
SHOW TABLES;

-- 2. Check available customers before inserting
SELECT CustomerId, FirstName, LastName, Email FROM Customers;

-- ============================================================
-- SAMPLE DATA
-- IMPORTANT:
-- This script reuses the first existing customer.
-- Confirm Customers has at least one row before running inserts.
-- Run the insert section only once to avoid duplicate test data.
-- ============================================================

SET @customer_id = (SELECT MIN(CustomerId) FROM Customers);
SELECT @customer_id AS CustomerId;

START TRANSACTION;

-- Create two sample LoanDeals. These are new test deals.
INSERT INTO LoanDeals
(CustomerId, LoanType, LoanAmount, InterestRate, TenureMonths,
 EmiAmount, BankName, BankAccountNumber, IFSCCode, EmiDay, ApprovedAmount)
VALUES
(@customer_id, 'Home Loan', 2500000.00, 8.5000, 240,
 21691.00, 'HDFC Bank', 'PORTFOLIO-TEST-001', 'HDFC0001234', 5, 2500000.00);

SET @deal_home = LAST_INSERT_ID();

INSERT INTO LoanDeals
(CustomerId, LoanType, LoanAmount, InterestRate, TenureMonths,
 EmiAmount, BankName, BankAccountNumber, IFSCCode, EmiDay, ApprovedAmount)
VALUES
(@customer_id, 'Vehicle Loan', 800000.00, 9.2500, 60,
 16700.00, 'SBI', 'PORTFOLIO-TEST-002', 'SBIN0001234', 15, 800000.00);

SET @deal_vehicle = LAST_INSERT_ID();

-- Add disbursements for the two deals
INSERT INTO Disbursements
(DealId, DisburseAmount, BankPartner, DisbursementDate, Status)
VALUES
(@deal_home, 2500000.00, 'HDFC Bank', '2026-01-15 10:00:00', 'DISBURSED'),
(@deal_vehicle, 800000.00, 'SBI', '2026-02-10 11:00:00', 'DISBURSED');

-- Create active loan accounts with remaining principal balances
INSERT INTO LoanAccounts
(CustomerId, DealId, LoanAccountNo, LoanAmount, OutstandingPrincipal,
 LoanStatus, InterestRate, TenureMonths, EmiAmount, DisbursementDate,
 TotalPaidAmount)
VALUES
(@customer_id, @deal_home, 'PORTFOLIO-HOME-001', 2500000.00, 2200000.00,
 'ACTIVE', 8.5000, 240, 21691.00, '2026-01-15 10:00:00', 300000.00),
(@customer_id, @deal_vehicle, 'PORTFOLIO-VEHICLE-001', 800000.00, 500000.00,
 'ACTIVE', 9.2500, 60, 16700.00, '2026-02-10 11:00:00', 100000.00);

-- Add sample EMI schedules (illustrative development data)
INSERT INTO EmiSchedules
(LoanAccountId, InstallmentNo, DueDate, PrincipalAmount, InterestAmount,
 OpeningBalance, ClosingBalance, Emi, PaymentStatus, PaidDate)
SELECT LoanAccountId, 1, DATE(DisbursementDate) + INTERVAL 1 MONTH,
       10000.00, 5000.00, LoanAmount, LoanAmount - 10000.00,
       EmiAmount, 'PAID', DATE_ADD(DisbursementDate, INTERVAL 1 MONTH)
FROM LoanAccounts
WHERE LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001');

-- Add sample payment records
INSERT INTO LoanPayments
(LoanAccountId, PaymentAmount, PaymentDate, PaymentStatus, PaymentName)
SELECT LoanAccountId, 15000.00, DATE_ADD(DisbursementDate, INTERVAL 1 MONTH),
       'SUCCESS', 'Portfolio report test payment'
FROM LoanAccounts
WHERE LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001');

COMMIT;

-- ============================================================
-- VERIFY TEST DATA
-- ============================================================

SELECT
    la.LoanAccountId,
    la.LoanAccountNo,
    ld.LoanType AS Product,
    la.LoanAmount,
    la.OutstandingPrincipal,
    la.LoanStatus,
    la.DisbursementDate
FROM LoanAccounts la
JOIN LoanDeals ld ON ld.DealId = la.DealId
WHERE la.LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001')
ORDER BY ld.LoanType;

-- Summary for portfolio report (as of 2026-12-31)
SELECT
    ld.LoanType AS Product,
    COUNT(*) AS ActiveLoans,
    SUM(la.OutstandingPrincipal) AS OutstandingAmount
FROM LoanAccounts la
JOIN LoanDeals ld ON ld.DealId = la.DealId
WHERE UPPER(TRIM(la.LoanStatus)) = 'ACTIVE'
  AND la.DisbursementDate < '2027-01-01'
  AND la.LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001')
GROUP BY ld.LoanType;

-- Total outstanding and product share
SELECT
    ld.LoanType AS Product,
    COUNT(*) AS ActiveLoans,
    SUM(la.OutstandingPrincipal) AS OutstandingAmount,
    ROUND(
        SUM(la.OutstandingPrincipal) /
        NULLIF((
            SELECT SUM(la2.OutstandingPrincipal)
            FROM LoanAccounts la2
            WHERE UPPER(TRIM(la2.LoanStatus)) = 'ACTIVE'
              AND la2.DisbursementDate < '2027-01-01'
              AND la2.LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001')
        ), 0) * 100, 2
    ) AS Percentage
FROM LoanAccounts la
JOIN LoanDeals ld ON ld.DealId = la.DealId
WHERE UPPER(TRIM(la.LoanStatus)) = 'ACTIVE'
  AND la.DisbursementDate < '2027-01-01'
  AND la.LoanAccountNo IN ('PORTFOLIO-HOME-001', 'PORTFOLIO-VEHICLE-001')
GROUP BY ld.LoanType;
