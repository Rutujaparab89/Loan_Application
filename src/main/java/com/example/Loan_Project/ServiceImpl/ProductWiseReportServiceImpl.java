package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.ProductWiseReportResponse;
import com.example.Loan_Project.Service.ProductWiseReportService;
import com.example.Loan_Project.Repository.ProductWiseReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ProductWiseReportServiceImpl implements ProductWiseReportService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductWiseReportServiceImpl.class);

    private static final List<String> PRODUCTS =
            List.of("Home Loan", "Vehicle Loan");

    private final ProductWiseReportRepository productWiseReportRepository;

    public ProductWiseReportServiceImpl(ProductWiseReportRepository productWiseReportRepository) {
        this.productWiseReportRepository = productWiseReportRepository;
    }

    @Override
    public List<ProductWiseReportResponse> getProductWiseReport(
            LocalDate fromDate, LocalDate toDate) {

        validateDates(fromDate, toDate);

        List<ProductWiseReportResponse> report = new ArrayList<>();
        long totalApps = 0;
        long totalApproved = 0;
        long totalDisbursed = 0;

        for (String product : PRODUCTS) {
            long applications = productWiseReportRepository.countDealsWithDisbursements(product, fromDate, toDate);
            long approved = productWiseReportRepository.countApprovedDeals(product, fromDate, toDate);
            long disbursed = productWiseReportRepository.countDisbursedDeals(product, fromDate, toDate);

            report.add(new ProductWiseReportResponse(
                    product, applications, approved, disbursed,
                    percentage(disbursed, applications)));

            totalApps += applications;
            totalApproved += approved;
            totalDisbursed += disbursed;
        }

        report.add(new ProductWiseReportResponse(
                "Total", totalApps, totalApproved, totalDisbursed,
                percentage(totalDisbursed, totalApps)));

        log.info("Product wise report generated from {} to {}", fromDate, toDate);
        return report;
    }

    @Override
    public byte[] exportProductWiseReport(
            LocalDate fromDate, LocalDate toDate) {

        List<ProductWiseReportResponse> report =
                getProductWiseReport(fromDate, toDate);

        StringBuilder csv = new StringBuilder();
        csv.append("Product,Total Apps,Approved,Disbursed,Disbursement %\r\n");

        for (ProductWiseReportResponse row : report) {
            csv.append(csvCell(row.getProduct())).append(',')
                    .append(row.getTotalApps()).append(',')
                    .append(row.getApproved()).append(',')
                    .append(row.getDisbursed()).append(',')
                    .append(String.format(Locale.US, "%.2f%%",
                            row.getDisbursementPercentage()))
                    .append("\r\n");
        }

        log.debug("Product wise CSV generated with {} rows", report.size());
        return ("\\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
    }

    private double percentage(long disbursed, long totalApps) {
        if (totalApps == 0) {
            return 0.0;
        }
        return Math.round((double) disbursed / totalApps * 10000.0) / 100.0;
    }

    private String csvCell(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void validateDates(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate and toDate are required, and fromDate must not be after toDate");
        }
    }
}
