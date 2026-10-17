package com.example.Loan_Project.Controller;

import com.example.Loan_Project.DTO.LoanAccountViewResponse;
import com.example.Loan_Project.Repository.LoanDashboardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/loans")
public class LmsLoanViewReportController {
    private final LoanDashboardRepository repository;
    public LmsLoanViewReportController(LoanDashboardRepository repository) { this.repository = repository; }

    @GetMapping
    public List<LoanAccountViewResponse> getAllLoans() {
        return repository.findAllLoanAccounts();
    }
}
