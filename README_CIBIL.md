# Loan_Project - CIBIL Module

This version contains the CIBIL scoring implementation based on the existing Customer Registration flow.

## ID strategy

Java `Long` is used for IDs and MySQL `BIGINT` is used for the corresponding ID columns.

## Employment design

Customer Registration keeps the general employment type:
- SALARIED
- SELF_EMPLOYED
- BUSINESS

CIBIL uses a separate employment sector:
- GOVERNMENT = 200 points
- PRIVATE_SECTOR = 150 points
- SELF_EMPLOYED = 100 points

This prevents the CIBIL module from assuming that every salaried customer is a private-sector employee.

## CIBIL formula

CIBIL Score = Income Score + Employment Score + Age Score + FOIR Score

Maximum = 1000.

### Income
- < 25,000 = 100
- 25,000 to < 50,000 = 200
- 50,000 to 100,000 = 300
- > 100,000 = 400

### Employment sector
- GOVERNMENT = 200
- PRIVATE_SECTOR = 150
- SELF_EMPLOYED = 100

### Age
- 21-24 = 50
- 25-45 = 150
- 46-60 = 100
- Outside these ranges = 0

### FOIR
FOIR = Existing Monthly EMI / Gross Monthly Income * 100

- < 30% = 250
- 30% to 50% = 150
- > 50% = Not Eligible / Reject / Loan Amount 0

## Decision matrix

- 900-1000: EXCELLENT / AUTO_APPROVE / ELIGIBLE / Above 1 Crore
- 800-899: VERY_GOOD / AUTO_APPROVE / ELIGIBLE / 75L-1Cr
- 750-799: GOOD / MANUAL_REVIEW / ELIGIBLE / 50L-75L
- 700-749: AVERAGE / MANUAL_REVIEW / ELIGIBLE / 25L-50L
- 650-699: RISKY / MANUAL_REVIEW / ELIGIBLE / 10L-25L
- <650: REJECT / REJECT / NOT_ELIGIBLE / 0

## APIs

### Register customer
POST `/api/v1/customers/register`

Example:
```json
{
  "firstName": "Test",
  "lastName": "Customer",
  "age": 23,
  "email": "test@example.com",
  "password": "Password@123",
  "mobileNo": "9876543210",
  "panNo": "ABCDE1234F",
  "aadhaarNo": "123456789012",
  "employmentType": "SALARIED",
  "employmentSector": "GOVERNMENT",
  "monthlyIncome": 75000,
  "monthlyInvestment": 5000
}
```

### Calculate CIBIL
POST `/api/v1/cibil/calculate/{customerId}`

Request body:
```json
{
  "existingMonthlyEmi": 18750
}
```

Customer income, age, employment type and employment sector are fetched from the Customers table.

### Get latest CIBIL report
GET `/api/v1/cibil/customer/{customerId}`

## Example

Income = 75,000
Employment sector = GOVERNMENT
Age = 23
Existing EMI = 18,750

FOIR = 25%

Income score = 300
Employment score = 200
Age score = 50
FOIR score = 250

CIBIL = 800

Result = VERY_GOOD / AUTO_APPROVE / ELIGIBLE / 75L-1Cr

## Database

Run `database/01_LoanApp_CIBIL_setup.sql` after recreating the database. It creates the CIBIL-ready Customers and CibilReports tables with BIGINT CustomerId.

## Run

Update `src/main/resources/application.properties` with your MySQL username/password, then run:

Windows:
`mvnw.cmd spring-boot:run`

or:
`mvnw.cmd clean package`


## Added backend conventions

- API versioning: `/api/v1`
- Standard `ApiResponse<T>` wrapper for successful and error responses
- BCrypt password hashing during customer registration
- Global exception handling for validation, duplicate email, missing customer/report, and unexpected errors
- `MonthlyInvestment` retained in the Customer entity and registration flow
- Password is never returned in the customer registration response

### Main endpoints

POST `/api/v1/customers/register`

POST `/api/v1/cibil/calculate/{customerId}`

GET `/api/v1/cibil/customer/{customerId}`

## Rate limiting

All `/api/v1/**` endpoints are rate-limited per client IP. Current defaults:
- Customer registration: 5 requests/minute
- CIBIL calculation: 10 requests/minute
- CIBIL fetch: 30 requests/minute

When the limit is exceeded, the API returns HTTP 429 with `Retry-After`, `X-RateLimit-Limit`, and `X-RateLimit-Remaining` headers.

## CIBIL response caching

The latest CIBIL response is cached in memory for 5 minutes. A cache hit avoids a database read for `GET /api/v1/cibil/customer/{customerId}`. The cache is refreshed when a new CIBIL report is calculated. For a multi-instance production deployment, replace the in-memory cache with Redis so all instances share the same cache.

## Pagination and filtering

Pagination and filtering are intentionally not added because the current module exposes customer-specific CIBIL fetches rather than a customer/report listing API. If a future Customer Officer report API lists many CIBIL records, pagination and filters should be added to that listing endpoint.
