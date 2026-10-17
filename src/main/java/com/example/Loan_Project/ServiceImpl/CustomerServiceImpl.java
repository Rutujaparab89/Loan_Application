package com.example.Loan_Project.ServiceImpl;

import com.example.Loan_Project.DTO.CustomerRegistrationRequest;
import com.example.Loan_Project.DTO.CustomerRegistrationResponse;
import com.example.Loan_Project.Entity.Customer;
import com.example.Loan_Project.Exception.EmailAlreadyExistsException;
import com.example.Loan_Project.Repository.CustomerRepository;
import com.example.Loan_Project.Service.CustomerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerRegistrationResponse register(
            CustomerRegistrationRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered!");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setAge(request.getAge());
        customer.setEmail(request.getEmail());

        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customer.setMobileNo(request.getMobileNo());
        customer.setPanNo(request.getPanNo());
        customer.setAadhaarNo(request.getAadhaarNo());
        customer.setEmploymentType(request.getEmploymentType());
        customer.setMonthlyIncome(request.getMonthlyIncome());
        customer.setMonthlyInvestment(request.getMonthlyInvestment());
        customer.setCreatedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);

        return convertToResponse(savedCustomer);
    }

    private CustomerRegistrationResponse convertToResponse(Customer customer) {

        CustomerRegistrationResponse response = new CustomerRegistrationResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setAge(customer.getAge());
        response.setEmail(customer.getEmail());
        response.setMobileNo(customer.getMobileNo());
        response.setPanNo(customer.getPanNo());
        response.setAadhaarNo(customer.getAadhaarNo());
        response.setEmploymentType(customer.getEmploymentType());
        response.setMonthlyIncome(customer.getMonthlyIncome());
        response.setMonthlyInvestment(customer.getMonthlyInvestment());
        response.setIsEmailVerified(customer.getIsEmailVerified());

        // Password intentionally excluded from response.

        return response;
    }
}
