package com.example.Loan_Project.Service;

import com.example.Loan_Project.DTO.ProductWiseReportResponse;
import java.time.LocalDate;
import java.util.List;

public interface ProductWiseReportService {

    List<ProductWiseReportResponse> getProductWiseReport(
            LocalDate fromDate, LocalDate toDate);

    byte[] exportProductWiseReport(LocalDate fromDate, LocalDate toDate);
}
