package com.example.Loan_Project.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ScoreCards")
public class ScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScoreCardId")
    private Long scoreCardId;

    @Column(name = "CustomerId", nullable = false)
    private Long customerId;

    @Column(name = "CurrentStatus")
    private String currentStatus;

    @Column(name = "RejectionReason")
    private String rejectionReason;

    @Column(name = "AppliedDate")
    private LocalDateTime appliedDate;

    @Column(name = "CibilScore")
    private Integer cibilScore;

    @Column(name = "RiskCategory")
    private String riskCategory;

    @Column(name = "EligibleLoanAmount")
    private BigDecimal eligibleLoanAmount;

    public ScoreCard() {
    }

    public Long getScoreCardId() {
        return scoreCardId;
    }

    public void setScoreCardId(Long scoreCardId) {
        this.scoreCardId = scoreCardId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Integer getCibilScore() {
        return cibilScore;
    }

    public void setCibilScore(Integer cibilScore) {
        this.cibilScore = cibilScore;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    public BigDecimal getEligibleLoanAmount() {
        return eligibleLoanAmount;
    }

    public void setEligibleLoanAmount(BigDecimal eligibleLoanAmount) {
        this.eligibleLoanAmount = eligibleLoanAmount;
    }
}