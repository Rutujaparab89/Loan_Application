package com.example.Loan_Project.Repository;

import com.example.Loan_Project.DTO.LoanAccountViewResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoanDashboardRepository {

    private final EntityManager entityManager;

    public LoanDashboardRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** Returns loan-account rows used by the Total Loans dashboard View Report action. */
    @Transactional(readOnly = true)
    public List<LoanAccountViewResponse> findAllLoanAccounts() {
        String sql = """
                SELECT la.LoanAccountId, la.LoanAccountNo, la.CustomerId, la.DealId,
                       ld.LoanType AS product, la.LoanAmount, la.OutstandingPrincipal,
                       la.LoanStatus, la.InterestRate, la.TenureMonths, la.EmiAmount,
                       la.DisbursementDate, la.TotalPaidAmount
                FROM LoanAccounts la
                LEFT JOIN LoanDeals ld ON ld.DealId = la.DealId
                ORDER BY la.LoanAccountId DESC
                """;

        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<LoanAccountViewResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            Timestamp disbursement = toTimestamp(row[11]);
            Number accountId = (Number) row[0];
            Number customerId = (Number) row[2];
            Number dealId = (Number) row[3];
            Number tenure = (Number) row[9];

            result.add(new LoanAccountViewResponse(
                    accountId == null ? 0L : accountId.longValue(),
                    (String) row[1],
                    customerId == null ? 0L : customerId.longValue(),
                    dealId == null ? 0L : dealId.longValue(),
                    (String) row[4],
                    (java.math.BigDecimal) row[5],
                    (java.math.BigDecimal) row[6],
                    (String) row[7],
                    (java.math.BigDecimal) row[8],
                    tenure == null ? null : tenure.intValue(),
                    (java.math.BigDecimal) row[10],
                    disbursement == null ? null : disbursement.toLocalDateTime(),
                    (java.math.BigDecimal) row[12]
            ));
        }
        return result;
    }

    private Timestamp toTimestamp(Object value) {
        if (value == null) return null;
        if (value instanceof Timestamp timestamp) return timestamp;
        if (value instanceof LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        }
        return Timestamp.valueOf(value.toString());
    }
}
