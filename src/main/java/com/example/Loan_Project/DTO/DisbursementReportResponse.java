package com.example.Loan_Project.DTO;

import java.math.BigDecimal;

public class DisbursementReportResponse {

    private String month;
    private long loansDisbursed;
    private BigDecimal disbursementAmount;

    public DisbursementReportResponse() {
    }

    public DisbursementReportResponse(
            String month,
            long loansDisbursed,
            BigDecimal disbursementAmount) {
        this.month = month;
        this.loansDisbursed = loansDisbursed;
        this.disbursementAmount = disbursementAmount;
    }

    public String getMonth() {
        return month;
    }

    public long getLoansDisbursed() {
        return loansDisbursed;
    }

    public BigDecimal getDisbursementAmount() {
        return disbursementAmount;
    }
}
