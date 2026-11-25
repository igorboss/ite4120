# Work Diary – Ali Al Saedi

## 06.10.2026

**Time spent:** approximately 3 hours

### Activities
- Reviewed the ITE4120 course requirements and assessment criteria.
- Reviewed the course GitHub repository and the Animals Register example.
- Cloned the ITE4120 course repository to my local computer.
- Installed and configured the required development tools, including Homebrew, OpenJDK 25, Node.js, npm, and Docker.
- Configured GitHub Packages authentication using a classic Personal Access Token with `read:packages` permission.
- Configured npm access to the `@helex-solutions/ui` package.
- Installed the frontend dependencies.
- Started the backend and verified the REST API using Swagger UI.
- Started the frontend and verified that the Animals Register application runs locally.
- Reviewed the example application's structure, including the frontend, REST API, and mock registry.
- Coordinated with my group regarding the next course tasks and deadlines.

### Problems and corrections
- Docker Desktop required additional setup before the development environment could run correctly.
- The first GitHub Personal Access Token did not have the required `read:packages` permission.
- npm returned `401 Unauthorized` and `403 Forbidden` errors while accessing GitHub Packages.
- Created a new classic GitHub Personal Access Token with the correct `read:packages` scope and updated the npm configuration.
- After correcting the token configuration, `npm install` completed successfully and the frontend started correctly.

### AI assistance
- Used ChatGPT as a step-by-step guide for understanding the course requirements and setting up the development environment.
- Used AI assistance to diagnose Docker, Java, npm, and GitHub Packages configuration issues.
- Verified the suggested steps manually by running the commands and checking the resulting application.

### What I learned
- Understood the basic structure of the course template and how the frontend, backend, database, REST API, and registry integration relate to each other.
- Learned how GitHub Packages authentication is used to access the Helex UI dependency.
- Learned how to run and verify the example application locally.
- Understood that our own component must be specification-driven and that individual work must remain traceable through commits and the work diary.

### Next steps
- Agree with the group on our project domain and first user story.
- Prepare the project specification.
- Design the data model with at least three tables and a foreign-key relationship.
- Define the first business tests using Given / When / Then.

## 15.10.2026

**Time spent:** approximately 2 hours

### Activities
- Continued development of the Accessible Parking Permit Register with the group.
- Reviewed the parking permit specification and database schema.
- Confirmed that the permit is linked to a person rather than to a specific vehicle.
- Reviewed the three-table data model and the foreign-key relationships between permit applications, decisions, and issued permits.
- Refined two business tests into clear Given / When / Then scenarios.
- Defined the positive case where a permit can be issued after an `APPROVED` decision.
- Defined the negative case where issuing a permit without an `APPROVED` decision must be rejected.
- Added validation requirements for personal codes, decisions, permit numbers, and permit validity dates.
- Added consistent REST API error-handling requirements using `400 Bad Request`, `404 Not Found`, and `409 Conflict`.
- Coordinated the division of work with the group and helped establish shared GitHub repository access for group collaboration.

### Decisions and reasoning
- Kept the parking permit person-centric because the permit belongs to the person and is not tied to a specific vehicle.
- Used `409 Conflict` when a request is structurally valid but conflicts with the current application state, such as attempting to issue a permit without an approved decision.
- Kept the business tests in plain-language Given / When / Then format so they can later be converted into executable tests.

### Problems and corrections
- Initially inserted the validation section at the wrong position in the specification and accidentally replaced the document heading.
- Detected the problem using `git diff` before committing.
- Restored the file from Git and inserted the validation section again in the correct location.
- Verified the changes before creating the commit.

### AI assistance
- Used ChatGPT to review the specification structure and discuss suitable validation and error-handling rules.
- Used AI assistance to refine the business tests and explain the distinction between validation and REST API error handling.
- Used AI assistance for Git commands and to diagnose and correct the misplaced specification section.
- Reviewed and verified the suggested changes manually before committing them.

### What I learned
- Learned how business requirements can be expressed as Given / When / Then scenarios and later converted into automated tests.
- Better understood the difference between input validation, business-rule validation, and REST API error responses.
- Learned why `git diff` should be used to review changes before staging and committing them.
- Improved my understanding of collaborative Git workflows and the importance of traceable individual commits.

### Next steps
- Review the group members' contributions and integrate them into the project.
- Prepare the REST API/OpenAPI requirements and API surface for the parking component.
- Convert the business tests into executable tests during the next development stage.
- Verify the required X-Road integration approach for the project.

## 26.10.2026

**Time spent:** approximately 1 hour

### Work completed

- Reviewed the Accessible Parking Permit Register specification after the group discussion.
- Documented the agreed business flow and clarified the project scope.
- Agreed that the application submission channel is outside the scope of the Parking Register.
- Agreed that permit delivery to the applicant is outside the scope of the Parking Register.
- Defined `permit_application.status` transitions as `RECEIVED` → `DECIDED`.
- Confirmed that the decision outcome is stored separately as `APPROVED` or `REJECTED`.
- Defined `parking_permit.status` transitions as `ACTIVE` → `EXPIRED` or `REVOKED`.
- Confirmed that the parking permit belongs to a person and is not linked to a specific vehicle.
- Coordinated responsibilities within the group: Kadri focuses on REST API / OpenAPI, Arushi on UI and NFR review, and I focus on business rules, validation/error handling, and an automated business test.

### Decisions

- Keep the register focused on municipal processing rather than application submission or permit delivery.
- Keep application processing status separate from the actual decision outcome.
- Use explicit status transitions for applications and issued permits.
- Continue using the person-centric permit model.

### AI assistance

AI assistance was used to review the agreed business flow, structure the specification changes, and check consistency with the existing business rules.

### What I learned

I improved my understanding of separating system scope from external processes and of modelling workflow status separately from business decisions.

### Next steps

- Convert one business test from the specification into an executable automated test.
- Review the REST API / OpenAPI work when available.
- Integrate and review the group members' contributions.
### Automated business test implementation

**Time spent:** approximately 1.5 hours

#### Work completed

- Converted the first plain-language business test into an executable integration test: `ParkingBusinessRulesIT`.
- Implemented the positive scenario: a parking permit can be issued when the related application has an `APPROVED` decision.
- Ran the test before implementing the parking backend and observed the expected RED stage because `ParkingService` and `ParkingPermit` did not yet exist.
- Added the `ParkingPermit` model.
- Added `ParkingRepository` using the existing project repository pattern and PostgreSQL schema.
- Added `ParkingService` containing the business rule that a permit may only be issued when the application has an `APPROVED` decision.
- Used Testcontainers with PostgreSQL so the business test runs against the actual Liquibase database schema.
- Re-ran `ParkingBusinessRulesIT` after implementation and reached the GREEN stage with `BUILD SUCCESSFUL`.

#### Test-driven development result

`Specification → Business Test → RED → Implementation → GREEN`

The automated test now verifies that an approved application can result in an `ACTIVE` parking permit with an ID, unique permit number, and validity period.

#### What I learned

I learned how a plain-language Given/When/Then business test can be converted into an executable integration test. I also learned the separation between the repository layer, which accesses the database, and the service layer, which enforces business rules.

#### Next steps

- Review the implementation and Git diff before committing.
- Integrate the REST API/OpenAPI work with the business service when available.
- Review and implement the second business test together with the group.
### Second automated business test

**Time spent:** approximately 1 hour

#### Work completed

- Converted the second plain-language business test into an executable integration test.
- Implemented the negative scenario: a parking permit cannot be issued when the related application does not have an `APPROVED` decision.
- Added a separate application creation method so an application can exist without automatically receiving an approved decision.
- Verified that `ParkingService` rejects the permit issuance with a `ConflictException`.
- Added a database check to verify that no parking permit is created when the request is rejected.
- Followed the test-driven workflow: the test initially failed because the required `createApplication` method did not exist, then the implementation was added and the test reached GREEN.
- Ran the complete Gradle test suite after the implementation and received `BUILD SUCCESSFUL`.

#### Test-driven development result

`Specification → Business Test → RED → Implementation → GREEN`

The second automated business test now verifies both parts of the business rule: the request is rejected without an `APPROVED` decision, and no parking permit is stored in the database.

#### What I learned

I learned how to test a negative business scenario and how to verify not only that an exception is returned, but also that an invalid operation does not create data in the database.

#### Next steps

- Review and commit the second automated business test.
- Review the REST API/OpenAPI contribution when it becomes available.
- Participate in integration and final group testing.

### Parking database schema alignment

**Time spent:** approximately 1 hour

#### Work completed

- Reviewed the parking database migrations against the confirmed Accessible Parking Permit Register specification.
- Identified missing database constraints: application business status, one decision per application, and one permit per application.
- Added a new Liquibase migration instead of modifying the already released migrations.
- Added `status` to `permit_application` with default value `RECEIVED` and allowed values `RECEIVED` and `DECIDED`.
- Added a unique constraint to ensure that an application can have only one decision.
- Added a unique constraint to ensure that an application can have only one parking permit.
- Added `getApplicationStatus` to `ParkingRepository`.
- Added an automated business test verifying that a newly created application starts with `RECEIVED` status.
- Ran the complete Gradle test suite after the changes and received `BUILD SUCCESSFUL`.

#### Result

The database schema is now better aligned with the confirmed specification, and the initial `RECEIVED` application status is verified by an automated integration test.

#### What I learned

I learned why released Liquibase changesets should remain immutable and why database changes should be introduced through a new migration. I also learned how database constraints can enforce business rules such as one decision and one permit per application.

#### Next steps

- Review the remaining REST API operations against the confirmed specification.
- Integrate application decision handling with the `DECIDED` status transition.
- Continue final integration and testing with the group.

### Parking application decision API

**Date:** 25 November 2026

#### Work completed

- Implemented `POST /api/v1/permit-applications/{id}/decision` to record application decisions.
- Added `ParkingDecisionRequest` with validation for the decision date and decision type.
- Added repository support for storing `APPROVED` and `REJECTED` decisions, including the decision reason.
- Added transactional service logic to validate applications, prevent duplicate decisions, and update the application status from `RECEIVED` to `DECIDED`.
- Added API documentation for HTTP 201, 400, 404, and 409 responses.
- Added three automated integration tests covering approved decisions, rejected decisions, and duplicate-decision conflicts.
- Verified each new test individually using Gradle and PostgreSQL Testcontainers.
- Ran the complete Gradle test suite and received `BUILD SUCCESSFUL`.

#### Result

The application decision API supports recording approval and rejection decisions, updates the application status, and prevents a second decision from replacing the original decision. All existing and new Gradle tests passed.

#### What I learned

I learned how to connect REST request validation, transactional business logic, and PostgreSQL persistence. I also learned how integration tests can verify HTTP responses, database changes, and conflict handling together.

#### Next steps

- Review the Git changes and create a pull request.
- Complete integration checks with the group's remaining components.
