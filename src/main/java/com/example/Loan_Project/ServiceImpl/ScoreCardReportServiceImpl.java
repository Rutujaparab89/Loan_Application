package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.ScoreCardReportResponse;
import com.example.Loan_Project.Entity.ScoreCard;
import com.example.Loan_Project.Repository.ScoreCardRepository;
import com.example.Loan_Project.Service.ScoreCardReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ScoreCardReportServiceImpl implements ScoreCardReportService {

    private static final Logger log =
            LoggerFactory.getLogger(ScoreCardReportServiceImpl.class);

    private final ScoreCardRepository scoreCardRepository;

    public ScoreCardReportServiceImpl(ScoreCardRepository scoreCardRepository) {
        this.scoreCardRepository = scoreCardRepository;
    }

    @Override
    public List<ScoreCardReportResponse> getScoreCardWiseReport(
            LocalDate fromDate, LocalDate toDate) {

        validateDates(fromDate, toDate);

        LocalDateTime startDate = fromDate.atStartOfDay();
        LocalDateTime endDate = toDate.plusDays(1).atStartOfDay();

        List<ScoreCard> scoreCards =
                scoreCardRepository
                        .findByAppliedDateGreaterThanEqualAndAppliedDateLessThan(
                                startDate, endDate);

        List<ScoreCardReportResponse> report = new ArrayList<>();

        report.add(generateCategoryReport(
                "EXCELLENT", "Excellent (A)", scoreCards));

        report.add(generateCategoryReport(
                "GOOD", "Good (B)", scoreCards));

        report.add(generateCategoryReport(
                "AVERAGE", "Average (C)", scoreCards));

        report.add(generateCategoryReport(
                "POOR", "Poor (D)", scoreCards));

        long applicants = 0;
        long approved = 0;
        long rejected = 0;
        long pending = 0;

        for (ScoreCardReportResponse row : report) {
            applicants += row.getApplicants();
            approved += row.getApproved();
            rejected += row.getRejected();
            pending += row.getPending();
        }

        report.add(new ScoreCardReportResponse(
                "Total",
                applicants,
                approved,
                rejected,
                pending,
                calculatePercentage(approved, applicants)
        ));

        log.info("Score card report generated from {} to {}",
                fromDate, toDate);

        return report;
    }

    @Override
    public byte[] exportScoreCardWiseReport(
            LocalDate fromDate, LocalDate toDate) {

        List<ScoreCardReportResponse> report =
                getScoreCardWiseReport(fromDate, toDate);

        StringBuilder csv = new StringBuilder();

        csv.append("Score Card,Applicants,Approved,Rejected,Pending,Approval %\r\n");

        for (ScoreCardReportResponse row : report) {
            csv.append(csvCell(row.getScoreCard())).append(',')
                    .append(row.getApplicants()).append(',')
                    .append(row.getApproved()).append(',')
                    .append(row.getRejected()).append(',')
                    .append(row.getPending()).append(',')
                    .append(String.format(
                            Locale.US,
                            "%.2f%%",
                            row.getApprovalPercentage()))
                    .append("\r\n");
        }

        log.debug("CSV report generated with {} rows", report.size());

        return ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
    }

    private ScoreCardReportResponse generateCategoryReport(
            String category, String label, List<ScoreCard> scoreCards) {

        long applicants = 0;
        long approved = 0;
        long rejected = 0;
        long pending = 0;

        for (ScoreCard scoreCard : scoreCards) {

            String riskCategory =
                    normalizeCategory(scoreCard.getRiskCategory());

            if (!category.equals(riskCategory)) {
                continue;
            }

            applicants++;

            String status = scoreCard.getCurrentStatus();

            if (isStatus(status, "APPROVED")) {
                approved++;
            } else if (isStatus(status, "REJECTED")) {
                rejected++;
            } else if (isPending(status)) {
                pending++;
            }
        }

        return new ScoreCardReportResponse(
                label,
                applicants,
                approved,
                rejected,
                pending,
                calculatePercentage(approved, applicants)
        );
    }

    private String normalizeCategory(String value) {

        if (value == null) {
            return "";
        }

        String category = value.trim().toUpperCase(Locale.ROOT);

        if (category.equals("EXCELLENT (A)") || category.equals("A")) {
            return "EXCELLENT";
        }

        if (category.equals("VERY_GOOD")
                || category.equals("GOOD (B)")
                || category.equals("B")) {
            return "GOOD";
        }

        if (category.equals("AVERAGE (C)") || category.equals("C")) {
            return "AVERAGE";
        }

        if (category.equals("POOR (D)")
                || category.equals("D")
                || category.equals("RISKY")
                || category.equals("REJECT")
                || category.equals("REJECTED")
                || category.equals("NOT_ELIGIBLE")) {
            return "POOR";
        }

        return category;
    }

    private boolean isStatus(String actual, String expected) {
        return actual != null
                && expected.equalsIgnoreCase(actual.trim());
    }

    private boolean isPending(String status) {

        if (status == null) {
            return false;
        }

        status = status.trim();

        return status.equalsIgnoreCase("PENDING")
                || status.equalsIgnoreCase("IN_PROGRESS")
                || status.equalsIgnoreCase("MANUAL_REVIEW");
    }

    private double calculatePercentage(long approved, long applicants) {

        if (applicants == 0) {
            return 0.0;
        }

        return Math.round((double) approved / applicants * 10000.0) / 100.0;
    }

    private String csvCell(String value) {

        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void validateDates(LocalDate fromDate, LocalDate toDate) {

        if (fromDate == null || toDate == null) {
            log.warn("Report dates are required");
            throw new IllegalArgumentException(
                    "fromDate and toDate are required");
        }

        if (fromDate.isAfter(toDate)) {
            log.warn("Invalid report date range: {} to {}",
                    fromDate, toDate);

            throw new IllegalArgumentException(
                    "fromDate must be before or equal to toDate");
        }
    }
}
