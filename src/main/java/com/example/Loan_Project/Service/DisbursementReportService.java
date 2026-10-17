package com.example.Loan_Project.Service;

import com.example.Loan_Project.DTO.DisbursementReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface DisbursementReportService {

    List<DisbursementReportResponse> getDisbursementReport(
            LocalDate fromDate,
            LocalDate toDate);

    byte[] exportDisbursementReport(LocalDate fromDate, LocalDate toDate);
}
