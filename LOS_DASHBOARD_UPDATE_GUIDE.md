LOS dashboard backend update guide

IDE: IntelliJ IDEA

Files to review/change:
1. src/main/java/com/example/Loan_Project/ServiceImpl/LosDashboardServiceImpl.java
   - This is the main place for LOS dashboard aggregation.
   - Current total/approved/rejected/pending and monthly trend are calculated from ScoreCards using AppliedDate.
   - Disbursed count is calculated from Disbursements using DisbursementDate and Status='DISBURSED'.
   - In Progress and Under Review currently use status aliases in ScoreCards. They should only be changed to DealReviews/LoanDeals queries after confirming the exact workflow statuses and how an application is linked to a deal. The supplied schema has no direct ScoreCards-to-LoanDeals foreign key, so joining only on CustomerId can duplicate counts.
2. src/main/java/com/example/Loan_Project/DTO/LosDashboardResponse.java
   - Already contains fields for inProgressApplications, underReviewApplications, disbursedApplications, applicationFunnel and monthlyTrend.
3. src/main/java/com/example/Loan_Project/Controller/LosDashboardController.java
   - Existing endpoint: GET /api/v1/dashboard/los?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD
   - No controller change is needed unless the response contract changes.

Frontend: no change is needed if the Angular dashboard already consumes these response fields. LMS code is separate and should remain unchanged.

Validation:
- Run the endpoint in Postman with the same fromDate/toDate as the UI.
- Compare totalApplications/approved/rejected/pending with ScoreCards.AppliedDate and CurrentStatus.
- Compare disbursedApplications with Disbursements where Status='DISBURSED' and DisbursementDate is in range.
- Keep in mind disbursements count disbursement records; it is not guaranteed to be a count of unique originating applications until a stable application-to-deal relationship exists.
