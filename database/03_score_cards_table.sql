-- Score Card Wise Report table setup.
-- Run against LoanApp after the Customers table exists.
-- Uses BIGINT CustomerId to match the Customers table in 01_LoanApp_CIBIL_setup.sql.
USE LoanApp;

CREATE TABLE IF NOT EXISTS ScoreCards (
    ScoreCardId BIGINT NOT NULL AUTO_INCREMENT,
    CustomerId BIGINT NOT NULL,
    CurrentStatus VARCHAR(100) NULL,
    RejectionReason VARCHAR(1000) NULL,
    AppliedDate DATETIME(6) NULL,
    CibilScore INT NULL,
    RiskCategory VARCHAR(50) NULL,
    EligibleLoanAmount DECIMAL(18,2) NULL,
    PRIMARY KEY (ScoreCardId),
    KEY IX_ScoreCards_CustomerId (CustomerId),
    CONSTRAINT FK_ScoreCards_Customers
        FOREIGN KEY (CustomerId) REFERENCES Customers(CustomerId)
) ENGINE=InnoDB;
