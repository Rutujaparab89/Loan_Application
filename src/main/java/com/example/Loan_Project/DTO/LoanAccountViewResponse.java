package com.example.Loan_Project.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanAccountViewResponse {
    private Long loanAccountId;
    private String loanAccountNo;
    private Long customerId;
    private Long dealId;
    private String product;
    private BigDecimal loanAmount;
    private BigDecimal outstandingPrincipal;
    private String loanStatus;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private LocalDateTime disbursementDate;
    private BigDecimal totalPaidAmount;

    public LoanAccountViewResponse(Long loanAccountId, String loanAccountNo, Long customerId, Long dealId,
            String product, BigDecimal loanAmount, BigDecimal outstandingPrincipal, String loanStatus,
            BigDecimal interestRate, Integer tenureMonths, BigDecimal emiAmount,
            LocalDateTime disbursementDate, BigDecimal totalPaidAmount) {
        this.loanAccountId = loanAccountId; this.loanAccountNo = loanAccountNo; this.customerId = customerId;
        this.dealId = dealId; this.product = product; this.loanAmount = loanAmount;
        this.outstandingPrincipal = outstandingPrincipal; this.loanStatus = loanStatus;
        this.interestRate = interestRate; this.tenureMonths = tenureMonths; this.emiAmount = emiAmount;
        this.disbursementDate = disbursementDate; this.totalPaidAmount = totalPaidAmount;
    }
    public Long getLoanAccountId() { return loanAccountId; }
    public String getLoanAccountNo() { return loanAccountNo; }
    public Long getCustomerId() { return customerId; }
    public Long getDealId() { return dealId; }
    public String getProduct() { return product; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public BigDecimal getOutstandingPrincipal() { return outstandingPrincipal; }
    public String getLoanStatus() { return loanStatus; }
    public BigDecimal getInterestRate() { return interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public BigDecimal getEmiAmount() { return emiAmount; }
    public LocalDateTime getDisbursementDate() { return disbursementDate; }
    public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
}
