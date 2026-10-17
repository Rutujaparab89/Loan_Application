package com.example.Loan_Project.DTO;

import com.example.Loan_Project.Entity.EmploymentType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRegistrationResponse {

    private Long customerId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private String mobileNo;
    private String panNo;
    private String aadhaarNo;
    private EmploymentType employmentType;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyInvestment;
    private Boolean isEmailVerified;
}
