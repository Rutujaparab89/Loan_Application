package com.example.Loan_Project.Repository;

import com.example.Loan_Project.DTO.DisbursementReportResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DisbursementReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public DisbursementReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DisbursementReportResponse> findMonthlyDisbursements(
            LocalDate fromDate,
            LocalDate toDate) {

        String sql = """
                SELECT DATE_FORMAT(DisbursementDate, '%b') AS monthName,
                       YEAR(DisbursementDate) AS reportYear,
                       MONTH(DisbursementDate) AS reportMonth,
                       COUNT(DISTINCT DealId) AS loansDisbursed,
                       COALESCE(SUM(DisburseAmount), 0) AS disbursementAmount
                FROM Disbursements
                WHERE DisbursementDate >= ?
                  AND DisbursementDate < DATE_ADD(?, INTERVAL 1 DAY)
                  AND UPPER(TRIM(Status)) = 'DISBURSED'
                GROUP BY YEAR(DisbursementDate), MONTH(DisbursementDate),
                         DATE_FORMAT(DisbursementDate, '%b')
                ORDER BY YEAR(DisbursementDate), MONTH(DisbursementDate)
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) ->
                new DisbursementReportResponse(
                        resultSet.getString("monthName"),
                        resultSet.getLong("loansDisbursed"),
                        resultSet.getBigDecimal("disbursementAmount")),
                Date.valueOf(fromDate),
                Date.valueOf(toDate));
    }
}
