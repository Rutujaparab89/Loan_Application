# LoanApp Angular Frontend - Credit Officer Pages Structure

LOS, LMS, and Reports dashboard components are located under:

- `src/app/features/credit-officer/pages/los/`
- `src/app/features/credit-officer/pages/lms/`
- `src/app/features/credit-officer/pages/reports-dashboard/`

LOS and LMS API services are colocated under their respective page folders in `services/`.
The duplicate legacy `src/app/features/los/` and `src/app/features/lms/` folders have been removed.

Routes remain `/officer/los`, `/officer/lms`, and `/officer/reports`.
The CIBIL page uses inline icons and an inline SVG score gauge, with no external image elements.
