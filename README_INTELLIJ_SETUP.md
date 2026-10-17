# LoanApp Backend — IntelliJ IDEA Setup

## Open the project
1. Extract this ZIP to a folder.
2. Open IntelliJ IDEA and choose **File → Open**. Select the extracted folder containing `pom.xml`.
3. When prompted, choose **Open as Project** and allow Maven to import dependencies.
4. Set the Project SDK to the Java version configured in `pom.xml` (use Java 21 if that is what the project specifies).
5. Open `src/main/resources/application.properties` and verify the MySQL URL, database name, username, and password.
6. Ensure MySQL is running and the `LoanApp` schema/tables exist.
7. Run the class annotated with `@SpringBootApplication` using the green Run icon.

## Verify
- Check the Run console for `Started ...` and confirm the configured server port (commonly 8080).
- Test the LOS/LMS dashboard endpoints using the paths documented in `README_DASHBOARD_APIS.md`.
- The frontend must call the same base URL/port as the running backend.

## Notes
- This ZIP is the backend only; it does not include IntelliJ itself or a database server.
- Do not commit real database passwords or secrets to Git.
- If Maven import fails, use the Maven tool window → Reload All Maven Projects.
