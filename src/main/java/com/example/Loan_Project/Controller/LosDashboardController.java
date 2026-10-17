package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.LosDashboardResponse;
import com.example.Loan_Project.Service.LosDashboardService;
import com.example.Loan_Project.Response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard/los")
public class LosDashboardController {
    private final LosDashboardService service;
    public LosDashboardController(LosDashboardService service) { this.service = service; }

    @GetMapping
    public ApiResponse<LosDashboardResponse> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        LocalDate end = toDate == null ? LocalDate.now() : toDate;
        LocalDate start = fromDate == null ? end.minusMonths(11).withDayOfMonth(1) : fromDate;
        if (start.isAfter(end)) throw new IllegalArgumentException("fromDate must be on or before toDate");
        return ApiResponse.success("LOS dashboard data fetched successfully", service.getDashboard(start, end));
    }
}
