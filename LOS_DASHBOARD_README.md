# LOS Dashboard Frontend

## Route
- `http://localhost:4200/los/dashboard`
- The root route (`/`) redirects to the LOS dashboard.

## Backend endpoint
- `GET http://localhost:8080/api/v1/dashboard/los?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD`
- Expected standard API wrapper:

```json
{
  "success": true,
  "message": "LOS dashboard data fetched successfully",
  "data": {
    "totalApplications": 0,
    "approvedApplications": 0,
    "rejectedApplications": 0,
    "pendingApplications": 0,
    "applicationFunnel": {},
    "monthlyTrend": []
  },
  "error": null
}
```

The dashboard reads `data` from the wrapper. If the backend returns an unsuccessful response or HTTP error, it shows an error message in the UI. HTTP 429 is handled with a rate-limit message.

## Run
1. Start the Spring Boot backend on port `8080`.
2. In this folder run `npm install` (or `npm ci`).
3. Run `npm start`.
4. Open `http://localhost:4200/los/dashboard`.

If the browser blocks the API call, configure Spring Boot CORS to allow `http://localhost:4200`.
