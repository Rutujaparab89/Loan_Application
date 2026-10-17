LoanApp CIBIL frontend — fixed layout / no backend changes

Project root: Loan_Project_Frontend/Loan_Project_Frontend1

Run:
  npm install
  ng serve

CIBIL page: http://localhost:4200/customer/cibil
Backend APIs remain unchanged:
  GET  http://localhost:8080/api/v1/cibil/customer/1
  POST http://localhost:8080/api/v1/cibil/calculate/1

This frontend uses Customer ID 1 for local development. Ensure that customer exists in your existing backend database.

Fixes included:
- Sidebar remains full-height and sticky while the page scrolls.
- Loading and Checking state flags are reset using RxJS finalize(), on success and on error.
- Existing CIBIL result fields, gauge, eligibility and API service are retained.
- No backend source or business logic is modified.
