-- OPTIONAL TEST DATA ONLY. Run only after confirming CustomerId values exist.
-- This script assumes Customers with IDs 1, 2, 3, 4 and 5 already exist.
-- Do not run against production data. Remove these records after testing if appropriate.
USE LoanApp;

INSERT INTO ScoreCards
    (CustomerId, CurrentStatus, RejectionReason, AppliedDate, CibilScore, RiskCategory, EligibleLoanAmount)
VALUES
    (1, 'APPROVED', NULL, '2024-01-10 10:30:00', 850, 'EXCELLENT', 7500000.00),
    (2, 'APPROVED', NULL, '2024-02-15 11:00:00', 820, 'EXCELLENT', 7500000.00),
    (3, 'REJECTED', 'High FOIR', '2024-03-05 14:20:00', 680, 'AVERAGE', 0.00),
    (4, 'PENDING', NULL, '2024-04-12 09:45:00', 720, 'AVERAGE', 2500000.00),
    (5, 'APPROVED', NULL, '2024-05-20 16:10:00', 780, 'GOOD', 5000000.00);

-- Verify:
-- SELECT * FROM ScoreCards ORDER BY ScoreCardId DESC;
-- SELECT DISTINCT RiskCategory FROM ScoreCards;
-- SELECT DISTINCT CurrentStatus FROM ScoreCards;

-- Delete ONLY these test rows after testing by their generated ScoreCardId values.
-- First inspect the inserted rows and delete their exact IDs; avoid DELETE FROM ScoreCards if real rows exist.
