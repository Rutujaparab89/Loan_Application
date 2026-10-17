package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.DisbursementReportResponse;
import com.example.Loan_Project.Repository.DisbursementReportRepository;
import com.example.Loan_Project.Service.DisbursementReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisbursementReportServiceImpl implements DisbursementReportService {

    private final DisbursementReportRepository repository;

    public DisbursementReportServiceImpl(DisbursementReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DisbursementReportResponse> getDisbursementReport(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("fromDate and toDate are required");
        }

        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("toDate must be on or after fromDate");
        }

        List<DisbursementReportResponse> monthlyRows =
                repository.findMonthlyDisbursements(fromDate, toDate);

        long totalLoansDisbursed = 0;
        BigDecimal totalDisbursementAmount = BigDecimal.ZERO;

        for (DisbursementReportResponse row : monthlyRows) {
            totalLoansDisbursed += row.getLoansDisbursed();
            totalDisbursementAmount = totalDisbursementAmount.add(
                    row.getDisbursementAmount());
        }


        List<DisbursementReportResponse> reportRows =
                new ArrayList<>(monthlyRows);

        reportRows.add(new DisbursementReportResponse(
                "Total",
                totalLoansDisbursed,
                totalDisbursementAmount));

        return reportRows;
    }

    @Override
    public byte[] exportDisbursementReport(LocalDate fromDate, LocalDate toDate) {

        List<DisbursementReportResponse> reportRows =
                getDisbursementReport(fromDate, toDate);

        StringBuilder csv = new StringBuilder();
        csv.append("Month,Loans Disbursed,Disbursement Amount (INR)\n");

        for (DisbursementReportResponse row : reportRows) {
            csv.append(escapeCsv(row.getMonth())).append(',');
            csv.append(row.getLoansDisbursed()).append(',');
            csv.append(row.getDisbursementAmount()).append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
