package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.LosDashboardResponse;
import com.example.Loan_Project.Entity.ScoreCard;
import com.example.Loan_Project.Repository.ScoreCardRepository;
import com.example.Loan_Project.Service.LosDashboardService;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class LosDashboardServiceImpl implements LosDashboardService {

    private final ScoreCardRepository scoreCardRepository;
    private final EntityManager entityManager;

    public LosDashboardServiceImpl(ScoreCardRepository scoreCardRepository, EntityManager entityManager) {
        this.scoreCardRepository = scoreCardRepository;
        this.entityManager = entityManager;
    }

    @Override
    public LosDashboardResponse getDashboard(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate and toDate are required, and fromDate must not be after toDate");
        }

        List<ScoreCard> applications = scoreCardRepository
                .findByAppliedDateGreaterThanEqualAndAppliedDateLessThan(
                        fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());

        long approved = countStatus(applications, "APPROVED");
        long rejected = countStatus(applications, "REJECTED");
        long pending = applications.size() - approved - rejected;

        long inProgress = applications.stream()
                .filter(a -> isOneOf(normalize(a.getCurrentStatus()), "APPLIED", "IN_PROGRESS", "IN PROGRESS", "ELIGIBLE"))
                .count();
        long underReview = applications.stream()
                .filter(a -> isOneOf(normalize(a.getCurrentStatus()), "PENDING", "UNDER_REVIEW", "UNDER REVIEW", "IN_REVIEW", "IN REVIEW"))
                .count();
        long disbursed = countDisbursements(fromDate, toDate);

        Map<String, Long> funnel = new java.util.LinkedHashMap<>();
        funnel.put("APPLICATIONS", (long) applications.size());
        funnel.put("IN_PROGRESS", inProgress);
        funnel.put("UNDER_REVIEW", underReview);
        funnel.put("APPROVED", approved);
        funnel.put("DISBURSED", disbursed);

        Map<Integer, Long> countsByMonth = applications.stream()
                .filter(application -> application.getAppliedDate() != null)
                .collect(Collectors.groupingBy(
                        application -> application.getAppliedDate().getYear() * 100
                                + application.getAppliedDate().getMonthValue(),
                        TreeMap::new, Collectors.counting()));

        List<LosDashboardResponse.MonthlyApplicationCount> monthlyTrend = countsByMonth.entrySet()
                .stream()
                .map(entry -> new LosDashboardResponse.MonthlyApplicationCount(
                        entry.getKey() / 100, entry.getKey() % 100, entry.getValue()))
                .toList();

        return new LosDashboardResponse(
                applications.size(), approved, rejected, pending, inProgress, underReview, disbursed, funnel, monthlyTrend);
    }

    private long countStatus(List<ScoreCard> applications, String expectedStatus) {
        return applications.stream()
                .filter(application -> normalize(application.getCurrentStatus()).equals(expectedStatus))
                .count();
    }

    private long countDisbursements(LocalDate fromDate, LocalDate toDate) {
        Query query = entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM Disbursements
                WHERE UPPER(TRIM(Status)) = 'DISBURSED'
                  AND DisbursementDate >= :fromDate
                  AND DisbursementDate < :toExclusive
                """);
        query.setParameter("fromDate", fromDate.atStartOfDay());
        query.setParameter("toExclusive", toDate.plusDays(1).atStartOfDay());
        return ((Number) query.getSingleResult()).longValue();
    }

    private boolean isOneOf(String status, String... expected) {
        for (String value : expected) if (status.equals(value)) return true;
        return false;
    }

    private String normalize(String status) {
        return status == null || status.isBlank()
                ? "UNKNOWN" : status.trim().toUpperCase(Locale.ROOT);
    }
}
