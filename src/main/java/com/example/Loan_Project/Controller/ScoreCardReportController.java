package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.ScoreCardReportResponse;
import com.example.Loan_Project.Response.ApiResponse;
import com.example.Loan_Project.Service.ScoreCardReportService;

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
public class ScoreCardReportController {

    private static final Logger log =
            LoggerFactory.getLogger(ScoreCardReportController.class);

    private final ScoreCardReportService reportService;

    public ScoreCardReportController(ScoreCardReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/score-card-wise")
    public ResponseEntity<ApiResponse<Page<ScoreCardReportResponse>>> getReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Generating score card report from {} to {}",
                fromDate, toDate);

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be between 1 and 100");
        }

        List<ScoreCardReportResponse> report =
                reportService.getScoreCardWiseReport(fromDate, toDate);

        int start = (int) Math.min((long) page * size, report.size());
        int end = Math.min(start + size, report.size());

        Page<ScoreCardReportResponse> result = new PageImpl<>(
                report.subList(start, end),
                PageRequest.of(page, size),
                report.size()
        );

        log.info("Report generated successfully with {} total rows",
                result.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Score card wise report generated successfully",
                        result
                )
        );
    }

    @GetMapping(value = "/score-card-wise/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        log.info("Exporting score card report from {} to {}",
                fromDate, toDate);

        byte[] file =
                reportService.exportScoreCardWiseReport(fromDate, toDate);

        log.info("CSV export completed, file size: {} bytes", file.length);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=score-card-wise-report.csv")
                .contentLength(file.length)
                .body(file);
    }
}
