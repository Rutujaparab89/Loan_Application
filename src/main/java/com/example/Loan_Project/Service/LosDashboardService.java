package com.example.Loan_Project.Service;
import com.example.Loan_Project.DTO.LosDashboardResponse; import java.time.LocalDate;
public interface LosDashboardService { LosDashboardResponse getDashboard(LocalDate fromDate, LocalDate toDate); }
