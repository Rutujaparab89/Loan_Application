# IntelliJ IDEA: Fix `ClassFormatError` for CibilReport

This backend archive contains source code and does not include compiled `target` output. The error `java.lang.ClassFormatError: Invalid method Code length 0 in class file com/example/Loan_Project/Entity/CibilReport` normally indicates a bad/stale compiled class, not a need to change CIBIL business logic.

## Open and rebuild
1. Extract this ZIP to a normal writable folder.
2. In IntelliJ IDEA, choose **File > Open** and select the folder containing `pom.xml`.
3. Wait for Maven import to finish.
4. Stop any running Spring Boot instance.
5. In IntelliJ Terminal, from the folder containing `pom.xml`, run:
   - Windows: `mvnw.cmd clean compile` (or `mvn clean compile` if Maven is installed)
   - macOS/Linux: `./mvnw clean compile`
6. If compilation succeeds, run `LoanProjectApplication` again.

If it still happens, use **File > Invalidate Caches... > Invalidate and Restart**, then run `mvnw.cmd clean compile` again. Confirm `CibilReport.java` is a normal source file under `src/main/java/com/example/Loan_Project/Entity/` and not under `target`.

## Notes
- Keep your existing database and business logic unchanged.
- The application may require your own local MySQL credentials in `src/main/resources/application.properties`.
- Do not import the ZIP over your current project without backing up local changes first.
