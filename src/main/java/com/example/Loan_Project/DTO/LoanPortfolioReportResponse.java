package com.example.Loan_Project.DTO;

import java.math.BigDecimal;

public class LoanPortfolioReportResponse {

    private String product;
    private long activeLoans;
    private BigDecimal outstandingAmount;
    private double percentageOfOutstanding;

    public LoanPortfolioReportResponse() {
    }

    public LoanPortfolioReportResponse(String product, long activeLoans,
            BigDecimal outstandingAmount, double percentageOfOutstanding) {
        this.product = product;
        this.activeLoans = activeLoans;
        this.outstandingAmount = outstandingAmount;
        this.percentageOfOutstanding = percentageOfOutstanding;
    }

    public String getProduct() {
        return product;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public double getPercentageOfOutstanding() {
        return percentageOfOutstanding;
    }
}
