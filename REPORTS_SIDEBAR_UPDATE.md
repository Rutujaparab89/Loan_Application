# Reports sidebar update

- Added a single **Reports** item to the Loan Officer sidebar (no dropdown).
- Clicking Reports navigates to `/officer/reports`, which renders the existing combined LOS + LMS Report Dashboard.
- Existing `/officer/dashboard`, `/los/dashboard`, and `/lms/dashboard` routes are retained.
- No backend or database changes are included.

Run from this folder in VS Code:

```bash
npm install
npm start
```

Open `http://localhost:4200/officer/reports`.
