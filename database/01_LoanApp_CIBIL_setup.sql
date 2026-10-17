CREATE DATABASE IF NOT EXISTS LoanApp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE LoanApp;

CREATE TABLE IF NOT EXISTS Customers (
    CustomerId BIGINT NOT NULL AUTO_INCREMENT,
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NULL,
    Age INT NULL,
    Email VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NULL,
    MobileNo VARCHAR(20) NULL,
    PanNo VARCHAR(20) NOT NULL,
    AadhaarNo VARCHAR(30) NOT NULL,
    EmploymentType VARCHAR(100) NULL,
    EmploymentSector VARCHAR(50) NULL,
    MonthlyIncome DECIMAL(18,2) NULL,
    MonthlyInvestment DECIMAL(18,2) NULL,
    IsEmailVerified BOOLEAN NOT NULL DEFAULT FALSE,
    CreatedAt DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (CustomerId),
    UNIQUE KEY UX_Customers_Email (Email),
    UNIQUE KEY UX_Customers_PanNo (PanNo),
    UNIQUE KEY UX_Customers_AadhaarNo (AadhaarNo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS CibilReports (
    CibilReportId BIGINT NOT NULL AUTO_INCREMENT,
    CustomerId BIGINT NOT NULL,
    PanNo VARCHAR(20) NULL,
    Income DECIMAL(18,2) NOT NULL,
    EmploymentType VARCHAR(100) NOT NULL,
    EmploymentSector VARCHAR(50) NOT NULL,
    Age INT NOT NULL,
    ExistingMonthlyEmi DECIMAL(18,2) NOT NULL,
    Foir DECIMAL(5,2) NOT NULL,
    CibilScore INT NOT NULL,
    Status VARCHAR(50) NOT NULL,
    Decision VARCHAR(50) NOT NULL,
    Eligibility VARCHAR(50) NOT NULL,
    LoanAmount DECIMAL(18,2) NOT NULL,
    CheckDate DATETIME(6) NOT NULL,
    PRIMARY KEY (CibilReportId),
    KEY IX_CibilReports_CustomerId (CustomerId),
    CONSTRAINT FK_CibilReports_Customers
        FOREIGN KEY (CustomerId) REFERENCES Customers(CustomerId)
) ENGINE=InnoDB;
