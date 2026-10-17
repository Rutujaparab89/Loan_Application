package com.example.Loan_Project.Controller;

import com.example.Loan_Project.Response.ApiResponse;
import com.example.Loan_Project.DTO.CustomerRegistrationRequest;
import com.example.Loan_Project.DTO.CustomerRegistrationResponse;
import com.example.Loan_Project.Service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerRegistrationResponse>> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {

        CustomerRegistrationResponse response = customerService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", response));
    }
}
