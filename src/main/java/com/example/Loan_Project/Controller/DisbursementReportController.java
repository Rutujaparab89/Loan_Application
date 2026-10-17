package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.DisbursementReportResponse;
import com.example.Loan_Project.Response.ApiResponse;
import com.example.Loan_Project.Service.DisbursementReportService;
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
public class DisbursementReportController {

    private static final Logger log =
            LoggerFactory.getLogger(DisbursementReportController.class);

    private final DisbursementReportService reportService;

    public DisbursementReportController(DisbursementReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/disbursement")
    public ResponseEntity<ApiResponse<Page<DisbursementReportResponse>>> getReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be between 1 and 100");
        }

        log.info("Generating disbursement report from {} to {}", fromDate, toDate);

        List<DisbursementReportResponse> reportRows =
                reportService.getDisbursementReport(fromDate, toDate);

        int startIndex = (int) Math.min((long) page * size, reportRows.size());
        int endIndex = Math.min(startIndex + size, reportRows.size());

        Page<DisbursementReportResponse> paginatedReport = new PageImpl<>(
                reportRows.subList(startIndex, endIndex),
                PageRequest.of(page, size),
                reportRows.size());

        return ResponseEntity.ok(ApiResponse.success(
                "Disbursement report generated successfully", paginatedReport));
    }

    @GetMapping(value = "/disbursement/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        log.info("Exporting disbursement report from {} to {}", fromDate, toDate);

        byte[] csvFile = reportService.exportDisbursementReport(fromDate, toDate);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=disbursement-report.csv")
                .contentLength(csvFile.length)
                .body(csvFile);
    }
}
