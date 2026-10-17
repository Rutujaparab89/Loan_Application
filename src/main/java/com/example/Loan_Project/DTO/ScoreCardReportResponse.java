package com.example.Loan_Project.DTO;

public class ScoreCardReportResponse {

    private String scoreCard;
    private Long applicants;
    private Long approved;
    private Long rejected;
    private Long pending;
    private Double approvalPercentage;

    public ScoreCardReportResponse() {
    }

    public ScoreCardReportResponse(String scoreCard, Long applicants, Long approved,
                                   Long rejected, Long pending, Double approvalPercentage) {
        this.scoreCard = scoreCard;
        this.applicants = applicants;
        this.approved = approved;
        this.rejected = rejected;
        this.pending = pending;
        this.approvalPercentage = approvalPercentage;
    }

    public String getScoreCard() { return scoreCard; }
    public void setScoreCard(String scoreCard) { this.scoreCard = scoreCard; }
    public Long getApplicants() { return applicants; }
    public void setApplicants(Long applicants) { this.applicants = applicants; }
    public Long getApproved() { return approved; }
    public void setApproved(Long approved) { this.approved = approved; }
    public Long getRejected() { return rejected; }
    public void setRejected(Long rejected) { this.rejected = rejected; }
    public Long getPending() { return pending; }
    public void setPending(Long pending) { this.pending = pending; }
    public Double getApprovalPercentage() { return approvalPercentage; }
    public void setApprovalPercentage(Double approvalPercentage) { this.approvalPercentage = approvalPercentage; }
}
