package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.LoanPortfolioReportResponse;
import com.example.Loan_Project.Repository.LoanPortfolioReportRepository;
import com.example.Loan_Project.Service.LoanPortfolioReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanPortfolioReportServiceImpl implements LoanPortfolioReportService {

    private final LoanPortfolioReportRepository repository;

    public LoanPortfolioReportServiceImpl(LoanPortfolioReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LoanPortfolioReportResponse> getLoanPortfolioReport(LocalDate asOfDate) {
        if (asOfDate == null) {
            throw new IllegalArgumentException("asOfDate is required");
        }
        if (asOfDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("asOfDate cannot be in the future");
        }

        List<LoanPortfolioReportResponse> productRows = repository.findActivePortfolio(asOfDate);
        long totalActiveLoans = 0;
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        for (LoanPortfolioReportResponse row : productRows) {
            totalActiveLoans += row.getActiveLoans();
            totalOutstanding = totalOutstanding.add(row.getOutstandingAmount());
        }

        List<LoanPortfolioReportResponse> result = new ArrayList<>();
        for (LoanPortfolioReportResponse row : productRows) {
            double percentage = 0.0;
            if (totalOutstanding.compareTo(BigDecimal.ZERO) > 0) {
                percentage = row.getOutstandingAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalOutstanding, 2, java.math.RoundingMode.HALF_UP)
                        .doubleValue();
            }
            result.add(new LoanPortfolioReportResponse(
                    row.getProduct(), row.getActiveLoans(),
                    row.getOutstandingAmount(), percentage));
        }

        result.add(new LoanPortfolioReportResponse(
                "Total", totalActiveLoans, totalOutstanding,
                totalOutstanding.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0));
        return result;
    }

    @Override
    public byte[] exportLoanPortfolioReport(LocalDate asOfDate) {
        List<LoanPortfolioReportResponse> rows = getLoanPortfolioReport(asOfDate);
        StringBuilder csv = new StringBuilder();
        csv.append("As Of Date,Product,Active Loans,Outstanding (INR),Percentage of Outstanding\n");
        for (LoanPortfolioReportResponse row : rows) {
            csv.append(asOfDate).append(',')
                    .append(escapeCsv(row.getProduct())).append(',')
                    .append(row.getActiveLoans()).append(',')
                    .append(row.getOutstandingAmount()).append(',')
                    .append(row.getPercentageOfOutstanding()).append("%\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
