package com.example.Loan_Project.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Database access for Product Wise Report.
 * Uses the existing LoanDeals, DealReviews and Disbursements tables.
 * No schema changes or new report table are required.
 */
@Repository
public class ProductWiseReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductWiseReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countDealsWithDisbursements(
            String product, LocalDate fromDate, LocalDate toDate) {

        String sql = """
                SELECT COUNT(DISTINCT ld.DealId)
                FROM LoanDeals ld
                JOIN Disbursements d ON d.DealId = ld.DealId
                WHERE LOWER(REPLACE(ld.LoanType, ' ', '')) =
                      LOWER(REPLACE(?, ' ', ''))
                  AND d.DisbursementDate >= ?
                  AND d.DisbursementDate < ?
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class,
                product, fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay());
        return count == null ? 0 : count;
    }

    public long countApprovedDeals(
            String product, LocalDate fromDate, LocalDate toDate) {

        String sql = """
                SELECT COUNT(DISTINCT ld.DealId)
                FROM LoanDeals ld
                JOIN DealReviews dr ON dr.DealId = ld.DealId
                JOIN Disbursements d ON d.DealId = ld.DealId
                WHERE LOWER(REPLACE(ld.LoanType, ' ', '')) =
                      LOWER(REPLACE(?, ' ', ''))
                  AND d.DisbursementDate >= ?
                  AND d.DisbursementDate < ?
                  AND UPPER(TRIM(dr.Status)) = 'APPROVED'
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class,
                product, fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay());
        return count == null ? 0 : count;
    }

    public long countDisbursedDeals(
            String product, LocalDate fromDate, LocalDate toDate) {

        String sql = """
                SELECT COUNT(DISTINCT ld.DealId)
                FROM LoanDeals ld
                JOIN Disbursements d ON d.DealId = ld.DealId
                WHERE LOWER(REPLACE(ld.LoanType, ' ', '')) =
                      LOWER(REPLACE(?, ' ', ''))
                  AND d.DisbursementDate >= ?
                  AND d.DisbursementDate < ?
                  AND UPPER(TRIM(d.Status)) = 'DISBURSED'
                """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class,
                product, fromDate.atStartOfDay(),
                toDate.plusDays(1).atStartOfDay());
        return count == null ? 0 : count;
    }
}
