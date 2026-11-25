# Work Diary – Arushi David

## 15.10.2026

**Time spent:** approximately 2 hours

### Activities
- Reviewed the Accessible Parking Permit Register User Story and compared it with the existing data model from the municipal officer and business perspective.
- Checked that the application, decision and permit data model supports the main business flow described in the User Story.
- Identified open specification questions concerning application status, permit status and permit validity dates.
- Added UI requirements for creating and viewing applications, recording decisions, issuing permits, handling invalid permit issuance and viewing permit details.
- Documented the existing business rules and open business questions in the project specification.
- Reviewed the relevant TEHIK non-functional requirements for the Accessible Parking Permit Register.
- Added applicable non-functional requirements concerning REST API versioning, Liquibase database migrations, WCAG 2.2 Level AA accessibility, error handling, database relationships, personal data in logs and secure communication.

### Problems and corrections
- Identified that the allowed values and transitions for `permit_application.status` and `parking_permit.status` are not yet defined in the specification.
- Identified that the validation relationship between `valid_from` and `valid_until` needs clarification.
- Avoided defining new status values or business rules without agreement from the group.

### AI assistance
- Used AI assistance to help identify possible gaps in the specification and formulate UI requirements.

### What I learned
- Learned how a User Story can be traced to the data model, UI requirements and business rules.
- Learned the difference between functional and non-functional requirements.
- Learned that unclear business rules should be documented as open questions rather than assumed during implementation.
- Learned how TEHIK requirements such as accessibility, database versioning, error handling and personal-data protection can apply to an e-government information system.

### Next steps
- Discuss the open business questions with the group.
- Review any feedback from Ali and Kadri on the specification changes.
- Continue with the next implementation tasks assigned after Session 3.
## Initial implementation (November 2026)

**Time spent:** approximately 8 hours

### Activities
- Configured the local development environment, including Docker, PostgreSQL, Java, Gradle and the React frontend.
- Reviewed the Animals Register implementation as a reference for developing the Accessible Parking Permit Register.
- Reviewed the project specification and existing backend architecture to understand the implementation requirements.
- Implemented the parking permit application creation form using React, TypeScript and the existing Helex UI components.
- Implemented the REST API functionality required to create parking permit applications.
- Added input validation for the personal identity code and application date.
- Tested application creation through the frontend and backend.
- Submitted the initial implementation through pull request #10 in the group repository.

### Problems and corrections
- Encountered and resolved local development environment and dependency configuration issues.
- Troubleshot Docker, Gradle and frontend package installation issues.
- Identified areas requiring further alignment between the implementation and the updated project specification.

### AI assistance
- Used ChatGPT for development environment configuration, code generation, troubleshooting and implementation guidance.
- Reviewed and executed the suggested implementation locally.

### What I learned
- Learned how React frontend components communicate with a Spring Boot REST API.
- Gained practical experience with Git branches, commits and pull requests.
- Developed an understanding of how frontend validation and backend persistence work together.
- Learned how to configure and run a full-stack application in a local development environment.

### Next steps
- Align the application creation functionality with the confirmed specification.
- Address feedback received during peer review.

## 21.11.2026

**Time spent:** approximately 3.5 hours

### Activities
- Reviewed peer feedback on pull request #10.
- Integrated the latest specification changes from the group repository.
- Updated the application creation endpoint to `POST /api/v1/permit-applications`.
- Aligned JSON request field names with the confirmed API specification.
- Implemented support for the optional application reason in the frontend, service and repository layers.
- Reviewed the existing database migration to confirm that the reason field was already supported.
- Added integration tests covering application creation with and without a reason and rejection of an invalid personal identity code.
- Successfully compiled the backend, executed the Gradle tests and built the frontend.
- Updated the pull request description to reflect the revised implementation.
- Committed and pushed the updated implementation for peer review.

### Problems and corrections
- Corrected inconsistencies between the original API implementation and the confirmed specification.
- Resolved Docker availability and Windows file-locking issues affecting backend testing.
- Added the required Spring Boot testing dependency to support MockMvc integration tests.
- Resolved integration testing configuration issues and successfully reran the tests.

### AI assistance
- Used ChatGPT to assist with API alignment, code modifications, integration test generation and troubleshooting.
- Executed the tests locally to verify the implementation.

### What I learned
- Learned how API contracts influence both frontend and backend implementation.
- Gained experience with integration testing, request validation and database persistence.
- Learned how to incorporate peer-review feedback into an existing pull request.
- Developed a better understanding of Spring Boot testing dependencies and MockMvc.

### Next steps
- Integrate the remaining backend functionality developed by the group.
- Implement the remaining parking register user interfaces.

## 25.11.2026

**Time spent:** approximately 1.5 hours

### Activities
- Reviewed the latest contributions from Ali and Kadri and integrated their changes into my development branch.
- Reviewed the confirmed project specification, REST API controllers and backend data models.
- Implemented the parking application list, including status and date filters, sorting and pagination controls.
- Implemented the application details page displaying the personal identity code, application date, application status and application reason.
- Integrated the approval and rejection functionality with the existing decision API.
- Implemented the parking permit issuance form and display of issued permit details.
- Added warning and error messages for invalid permit issuance.
- Registered the new frontend routes using the existing component routing mechanism.
- Added navigation between the Animals Register and Parking Permit Register.
- Added back navigation and automatic redirection following successful application creation.
- Reviewed the implemented functionality against UI requirements UI-01 to UI-06.

### Functional testing
- Verified successful application creation and retrieval.
- Verified that newly created applications have the RECEIVED status.
- Verified the approval workflow and transition from RECEIVED to DECIDED.
- Successfully issued a parking permit following an approved decision and verified the generated permit number and ACTIVE status.
- Verified the rejection workflow.
- Verified that attempting to issue a permit for a rejected application displays an error and does not display an issued permit.
- Successfully tested the DECIDED and RECEIVED status filters and the application date-from filter.
- Successfully completed the frontend production build before and after the navigation changes.

### Problems and corrections
- Investigated HTTP 500 errors caused by the backend being unavailable.
- Resolved a Gradle resource-processing issue associated with the local Windows and OneDrive development environment.
- Restarted the backend and verified successful communication between the frontend and backend.
- Verified that the latest database migrations were successfully applied during backend startup.
- Identified and implemented navigation improvements to make the parking register easier to access and use.

### AI assistance
- Used ChatGPT to assist with generating React and TypeScript components, API integration code, PowerShell scripts, troubleshooting and functional test scenarios.
- Executed the generated scripts, reviewed the changes and manually tested the implemented functionality in the local development environment.

### What I learned
- Learned how to integrate frontend components with REST APIs developed by other team members.
- Gained practical experience implementing a complete business workflow across application creation, decision-making and permit issuance.
- Developed a better understanding of status transitions, business-rule enforcement and error handling.
- Learned how manual functional testing can complement automated backend integration tests.
- Gained experience integrating multiple team members' contributions through Git.

### Next steps
- Complete final navigation verification and submit the implementation for group review.
- Coordinate the integration of the frontend changes into the combined Parking Register pull request.
- Perform further accessibility and edge-case testing, including permit validity date verification.
- Prepare for the project defence on 27 November 2026.