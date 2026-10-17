package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.LmsDashboardResponse;
import com.example.Loan_Project.Service.LmsDashboardService;
import com.example.Loan_Project.Response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard/lms")
public class LmsDashboardController {
    private final LmsDashboardService service;
    public LmsDashboardController(LmsDashboardService service) { this.service = service; }

    @GetMapping
    public ApiResponse<LmsDashboardResponse> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        LocalDate end = toDate == null ? LocalDate.now() : toDate;
        LocalDate start = fromDate == null ? end.minusMonths(11).withDayOfMonth(1) : fromDate;
        if (start.isAfter(end)) throw new IllegalArgumentException("fromDate must be on or before toDate");
        return ApiResponse.success("LMS dashboard data fetched successfully", service.getDashboard(start, end));
    }
}
