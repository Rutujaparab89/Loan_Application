package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.DisbursementReportResponse;
import com.example.Loan_Project.DTO.LmsDashboardResponse;
import com.example.Loan_Project.DTO.LoanPortfolioReportResponse;
import com.example.Loan_Project.Repository.DisbursementReportRepository;
import com.example.Loan_Project.Repository.LoanPortfolioReportRepository;
import com.example.Loan_Project.Service.LmsDashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LmsDashboardServiceImpl implements LmsDashboardService {

    private final EntityManager entityManager;
    private final LoanPortfolioReportRepository portfolioRepository;
    private final DisbursementReportRepository disbursementRepository;

    public LmsDashboardServiceImpl(
            EntityManager entityManager,
            LoanPortfolioReportRepository portfolioRepository,
            DisbursementReportRepository disbursementRepository) {
        this.entityManager = entityManager;
        this.portfolioRepository = portfolioRepository;
        this.disbursementRepository = disbursementRepository;
    }

    @Override
    public LmsDashboardResponse getDashboard(LocalDate fromDate, LocalDate toDate) {
        validateDates(fromDate, toDate);

        long totalLoans = countLoans(null);
        long activeLoans = countLoans("ACTIVE");
        long closedLoans = countLoans("CLOSED");

        List<LoanPortfolioReportResponse> portfolio =
                portfolioRepository.findActivePortfolio(toDate);
        List<DisbursementReportResponse> disbursements =
                disbursementRepository.findMonthlyDisbursements(fromDate, toDate);
        List<LmsDashboardResponse.MonthlyCollectionEfficiency> collections =
                findMonthlyCollectionEfficiency(fromDate, toDate);

        return new LmsDashboardResponse(
                totalLoans, activeLoans, closedLoans,
                portfolio, disbursements, collections);
    }

    private long countLoans(String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM LoanAccounts"
                : "SELECT COUNT(*) FROM LoanAccounts WHERE UPPER(TRIM(LoanStatus)) = :status";

        Query query = entityManager.createNativeQuery(sql);
        if (status != null) {
            query.setParameter("status", status);
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    private List<LmsDashboardResponse.MonthlyCollectionEfficiency> findMonthlyCollectionEfficiency(
            LocalDate fromDate, LocalDate toDate) {
        String sql = """
                SELECT due.reportYear, due.reportMonth, due.monthName,
                       due.amountDue, COALESCE(paid.amountCollected, 0) AS amountCollected
                FROM (
                    SELECT YEAR(DueDate) AS reportYear,
                           MONTH(DueDate) AS reportMonth,
                           DATE_FORMAT(DueDate, '%b') AS monthName,
                           COALESCE(SUM(Emi), 0) AS amountDue
                    FROM EmiSchedules
                    WHERE DueDate >= :fromDate AND DueDate < :toExclusive
                    GROUP BY YEAR(DueDate), MONTH(DueDate), DATE_FORMAT(DueDate, '%b')
                ) due
                LEFT JOIN (
                    SELECT YEAR(PaymentDate) AS reportYear,
                           MONTH(PaymentDate) AS reportMonth,
                           COALESCE(SUM(PaymentAmount), 0) AS amountCollected
                    FROM LoanPayments
                    WHERE PaymentDate >= :fromDate AND PaymentDate < :toExclusive
                      AND UPPER(TRIM(PaymentStatus)) IN ('PAID', 'SUCCESS', 'COMPLETED')
                    GROUP BY YEAR(PaymentDate), MONTH(PaymentDate)
                ) paid ON paid.reportYear = due.reportYear
                      AND paid.reportMonth = due.reportMonth
                ORDER BY due.reportYear, due.reportMonth
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toExclusive", toDate.plusDays(1));

        List<Object[]> rows = query.getResultList();
        List<LmsDashboardResponse.MonthlyCollectionEfficiency> result = new ArrayList<>();

        for (Object[] row : rows) {
            BigDecimal due = decimal(row[3]);
            BigDecimal collected = decimal(row[4]);
            BigDecimal percentage = due.signum() == 0
                    ? BigDecimal.ZERO
                    : collected.multiply(BigDecimal.valueOf(100))
                            .divide(due, 2, RoundingMode.HALF_UP);

            result.add(new LmsDashboardResponse.MonthlyCollectionEfficiency(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue(),
                    (String) row[2], due, collected, percentage));
        }
        return result;
    }

    private BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private void validateDates(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate and toDate are required, and fromDate must not be after toDate");
        }
    }
}
