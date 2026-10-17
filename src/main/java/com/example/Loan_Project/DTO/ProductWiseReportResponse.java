package com.example.Loan_Project.DTO;

public class ProductWiseReportResponse {

    private String product;
    private long totalApps;
    private long approved;
    private long disbursed;
    private double disbursementPercentage;

    public ProductWiseReportResponse() {
    }

    public ProductWiseReportResponse(String product, long totalApps,
            long approved, long disbursed, double disbursementPercentage) {
        this.product = product;
        this.totalApps = totalApps;
        this.approved = approved;
        this.disbursed = disbursed;
        this.disbursementPercentage = disbursementPercentage;
    }

    public String getProduct() {
        return product;
    }

    public long getTotalApps() {
        return totalApps;
    }

    public long getApproved() {
        return approved;
    }

    public long getDisbursed() {
        return disbursed;
    }

    public double getDisbursementPercentage() {
        return disbursementPercentage;
    }
}
