# Customer CIBIL Score UI

This frontend adds the customer-side CIBIL Score and Eligibility Result pages to the existing Angular project.

## Routes
- `/customer/cibil` — CIBIL gauge, latest score, last checked time, and Check Score action.
- `/customer/cibil/result` — customer eligibility result with income, PAN (masked), score, loan amount and decision.

## Backend endpoints used
- `GET http://localhost:8080/api/v1/cibil/customer/{customerId}`
- `POST http://localhost:8080/api/v1/cibil/calculate/{customerId}`

The service expects the existing `ApiResponse` wrapper with the result in `data`, or a direct JSON result.

## Customer ID
The page reads the authenticated customer's ID from local storage using one of these keys: `customerId`, `customer_id`, `loggedInCustomerId`, `currentCustomerId`; it also checks `currentUser`, `user`, or `customer` JSON objects for `customerId`.

If your login flow stores the ID under another key, update `resolveCustomerId()` in `src/app/features/customer/pages/cibil-score/cibil-score.ts` and `getCustomerId()` in `cibil-eligibility-result.ts` to match your authentication response. The UI intentionally does not use demo score values.

## Run
From `Loan_Project_Frontend/Loan_Project_Frontend1`:

```bash
npm install
npm start
```

If Spring Boot runs on a different port, update `baseUrl` in `src/app/features/customer/services/cibil-api.ts`. Ensure the backend CORS configuration allows the Angular origin.
