package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.LoanPortfolioReportResponse;
import com.example.Loan_Project.Response.ApiResponse;
import com.example.Loan_Project.Service.LoanPortfolioReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class LoanPortfolioReportController {

    private static final Logger log = LoggerFactory.getLogger(LoanPortfolioReportController.class);
    private final LoanPortfolioReportService reportService;

    public LoanPortfolioReportController(LoanPortfolioReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/loan-portfolio")
    public ResponseEntity<ApiResponse<Page<LoanPortfolioReportResponse>>> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
        log.info("Generating loan portfolio report as of {}", asOfDate);
        List<LoanPortfolioReportResponse> rows = reportService.getLoanPortfolioReport(asOfDate);
        int start = (int) Math.min((long) page * size, rows.size());
        int end = Math.min(start + size, rows.size());
        Page<LoanPortfolioReportResponse> result = new PageImpl<>(
                rows.subList(start, end), PageRequest.of(page, size), rows.size());
        return ResponseEntity.ok(ApiResponse.success("Loan portfolio report generated successfully", result));
    }

    @GetMapping(value = "/loan-portfolio/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        log.info("Exporting loan portfolio report as of {}", asOfDate);
        byte[] file = reportService.exportLoanPortfolioReport(asOfDate);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=loan-portfolio-report.csv")
                .contentLength(file.length)
                .body(file);
    }
}
