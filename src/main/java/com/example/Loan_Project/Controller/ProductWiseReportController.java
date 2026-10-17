package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.ProductWiseReportResponse;
import com.example.Loan_Project.Response.ApiResponse;
import com.example.Loan_Project.Service.ProductWiseReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ProductWiseReportController {

    private static final Logger log =
            LoggerFactory.getLogger(ProductWiseReportController.class);

    private final ProductWiseReportService reportService;

    public ProductWiseReportController(ProductWiseReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/product-wise")
    public ResponseEntity<ApiResponse<Page<ProductWiseReportResponse>>> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be between 1 and 100");
        }

        log.info("Generating product wise report from {} to {}", fromDate, toDate);

        List<ProductWiseReportResponse> rows =
                reportService.getProductWiseReport(fromDate, toDate);

        int start = (int) Math.min((long) page * size, rows.size());
        int end = Math.min(start + size, rows.size());

        Page<ProductWiseReportResponse> result = new PageImpl<>(
                rows.subList(start, end), PageRequest.of(page, size), rows.size());

        return ResponseEntity.ok(ApiResponse.success(
                "Product wise report generated successfully", result));
    }

    @GetMapping(value = "/product-wise/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        log.info("Exporting product wise report from {} to {}", fromDate, toDate);

        byte[] file = reportService.exportProductWiseReport(fromDate, toDate);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=product-wise-report.csv")
                .contentLength(file.length)
                .body(file);
    }
}
