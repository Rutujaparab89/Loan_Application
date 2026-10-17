package com.example.Loan_Project.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CibilCalculationResponse {

    private Long customerId;

    private String customerName;

    private String panNo;

    private LocalDateTime checkDate;

    private BigDecimal eligibleLoanAmount;

    private BigDecimal income;

    private String employmentType;


    private Integer age;

    private BigDecimal foir;

    private Integer cibilScore;

    private String status;

    private String decision;

    private String eligibility;

    private BigDecimal loanAmount;
}