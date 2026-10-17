package com.example.Loan_Project.Service;

import com.example.Loan_Project.DTO.ScoreCardReportResponse;
import java.time.LocalDate;
import java.util.List;

public interface ScoreCardReportService {

    List<ScoreCardReportResponse> getScoreCardWiseReport(LocalDate fromDate, LocalDate toDate);

    byte[] exportScoreCardWiseReport(LocalDate fromDate, LocalDate toDate);
}
