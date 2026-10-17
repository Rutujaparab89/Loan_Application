package com.example.Loan_Project.DTO;

import java.util.List;
import java.util.Map;

public class LosDashboardResponse {
    private long totalApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long pendingApplications;
    private long inProgressApplications;
    private long underReviewApplications;
    private long disbursedApplications;
    private Map<String, Long> applicationFunnel;
    private List<MonthlyApplicationCount> monthlyTrend;

    public LosDashboardResponse(long totalApplications, long approvedApplications,
            long rejectedApplications, long pendingApplications, long inProgressApplications,
            long underReviewApplications, long disbursedApplications,
            Map<String, Long> applicationFunnel, List<MonthlyApplicationCount> monthlyTrend) {
        this.totalApplications = totalApplications;
        this.approvedApplications = approvedApplications;
        this.rejectedApplications = rejectedApplications;
        this.pendingApplications = pendingApplications;
        this.inProgressApplications = inProgressApplications;
        this.underReviewApplications = underReviewApplications;
        this.disbursedApplications = disbursedApplications;
        this.applicationFunnel = applicationFunnel;
        this.monthlyTrend = monthlyTrend;
    }

    public long getTotalApplications() { return totalApplications; }
    public long getApprovedApplications() { return approvedApplications; }
    public long getRejectedApplications() { return rejectedApplications; }
    public long getPendingApplications() { return pendingApplications; }
    public long getInProgressApplications() { return inProgressApplications; }
    public long getUnderReviewApplications() { return underReviewApplications; }
    public long getDisbursedApplications() { return disbursedApplications; }
    public Map<String, Long> getApplicationFunnel() { return applicationFunnel; }
    public List<MonthlyApplicationCount> getMonthlyTrend() { return monthlyTrend; }

    public static class MonthlyApplicationCount {
        private int year;
        private int month;
        private long applications;
        public MonthlyApplicationCount(int year, int month, long applications) {
            this.year = year; this.month = month; this.applications = applications;
        }
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public long getApplications() { return applications; }
    }
}
