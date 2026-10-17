package com.example.Loan_Project.Repository;

import com.example.Loan_Project.DTO.LoanPortfolioReportResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class LoanPortfolioReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoanPortfolioReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LoanPortfolioReportResponse> findActivePortfolio(LocalDate asOfDate) {
        String sql = """
                SELECT ld.LoanType AS product,
                       COUNT(la.LoanAccountId) AS activeLoans,
                       COALESCE(SUM(la.OutstandingPrincipal), 0) AS outstandingAmount
                FROM LoanAccounts la
                JOIN LoanDeals ld ON ld.DealId = la.DealId
                WHERE UPPER(la.LoanStatus) = 'ACTIVE'
                  AND la.DisbursementDate < DATE_ADD(?, INTERVAL 1 DAY)
                GROUP BY ld.LoanType
                ORDER BY ld.LoanType
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new LoanPortfolioReportResponse(
                        rs.getString("product"),
                        rs.getLong("activeLoans"),
                        rs.getBigDecimal("outstandingAmount"),
                        0.0), Date.valueOf(asOfDate));
    }
}
