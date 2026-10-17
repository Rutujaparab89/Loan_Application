package com.example.Loan_Project.DTO;

import java.math.BigDecimal;
import java.util.List;

public class LmsDashboardResponse {
    private long totalLoans;
    private long activeLoans;
    private long closedLoans;
    private List<LoanPortfolioReportResponse> portfolio;
    private List<DisbursementReportResponse> monthlyDisbursements;
    private List<MonthlyCollectionEfficiency> monthlyCollectionEfficiency;

    public LmsDashboardResponse(long totalLoans, long activeLoans, long closedLoans,
            List<LoanPortfolioReportResponse> portfolio,
            List<DisbursementReportResponse> monthlyDisbursements,
            List<MonthlyCollectionEfficiency> monthlyCollectionEfficiency) {
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.closedLoans = closedLoans;
        this.portfolio = portfolio;
        this.monthlyDisbursements = monthlyDisbursements;
        this.monthlyCollectionEfficiency = monthlyCollectionEfficiency;
    }
    public long getTotalLoans() { return totalLoans; }
    public long getActiveLoans() { return activeLoans; }
    public long getClosedLoans() { return closedLoans; }
    public List<LoanPortfolioReportResponse> getPortfolio() { return portfolio; }
    public List<DisbursementReportResponse> getMonthlyDisbursements() { return monthlyDisbursements; }
    public List<MonthlyCollectionEfficiency> getMonthlyCollectionEfficiency() { return monthlyCollectionEfficiency; }

    public static class MonthlyCollectionEfficiency {
        private int year;
        private int month;
        private String monthName;
        private BigDecimal amountDue;
        private BigDecimal amountCollected;
        private BigDecimal efficiencyPercentage;
        public MonthlyCollectionEfficiency(int year, int month, String monthName,
                BigDecimal amountDue, BigDecimal amountCollected, BigDecimal efficiencyPercentage) {
            this.year = year; this.month = month; this.monthName = monthName;
            this.amountDue = amountDue; this.amountCollected = amountCollected;
            this.efficiencyPercentage = efficiencyPercentage;
        }
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public String getMonthName() { return monthName; }
        public BigDecimal getAmountDue() { return amountDue; }
        public BigDecimal getAmountCollected() { return amountCollected; }
        public BigDecimal getEfficiencyPercentage() { return efficiencyPercentage; }
    }
}
