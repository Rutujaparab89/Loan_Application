package com.example.Loan_Project.Service;

import com.example.Loan_Project.DTO.CustomerRegistrationRequest;
import com.example.Loan_Project.DTO.CustomerRegistrationResponse;

public interface CustomerService {

    CustomerRegistrationResponse register(CustomerRegistrationRequest customerRegistrationRequest);
}
