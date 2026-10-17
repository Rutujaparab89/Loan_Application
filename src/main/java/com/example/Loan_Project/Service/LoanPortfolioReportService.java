package com.example.Loan_Project.Service;

import com.example.Loan_Project.DTO.LoanPortfolioReportResponse;
import java.time.LocalDate;
import java.util.List;

public interface LoanPortfolioReportService {
    List<LoanPortfolioReportResponse> getLoanPortfolioReport(LocalDate asOfDate);
    byte[] exportLoanPortfolioReport(LocalDate asOfDate);
}
