# PARKING.01 · Accessible Parking Permit Register

> **PARKING.01** · state **Confirmed** · traces from [PARKING-US-001](../user-stories/PARKING-US-001.md)

## Description

A register for accessible parking permit applications, decisions and issued permits.

The permit is linked to a person, not to a specific vehicle. The register stores only the person's personal identity code. Current personal information, such as name and address, is retrieved from the Population Register through a registry adapter, using X-Road in production. The Population Register remains the source of truth for personal data.

A municipal officer creates a permit application, records an approval or rejection decision and, when the application is approved, issues an accessible parking permit.

## Data model *(→ session 2)*

| Table | Field | Type | Rules |
| --- | --- | --- | --- |
| `parking.permit_application` | `id` | bigint | PK |
| | `personal_code` | varchar(11) | required |
| | `application_date` | date | required |
| | `status` | varchar(30) | required; system sets to `RECEIVED` on creation and `DECIDED` when the application's single decision is recorded |
| | `reason` | varchar(500) | optional |
| `parking.permit_decision` | `id` | bigint | PK |
| | `application_id` | bigint | required; FK → `permit_application.id`; unique (one decision per application) |
| | `decision_date` | date | required |
| | `decision_type` | varchar(20) | required; `APPROVED` or `REJECTED` |
| | `decision_reason` | varchar(500) | optional |
| `parking.parking_permit` | `id` | bigint | PK |
| | `application_id` | bigint | required; FK → `permit_application.id`; unique (one permit per application) |
| | `permit_number` | varchar(50) | required; unique; generated as `PARK-` plus 12 uppercase alphanumeric characters |
| | `valid_from` | date | required |
| | `valid_until` | date | required |
| | `status` | varchar(20) | required; set to `ACTIVE` on issuance |

## Validation and error handling

The system must validate all requests before storing or changing data.

- `personal_code` is required and must contain exactly 11 digits.
- A decision can only be recorded for an existing permit application.
- `decision_type` must be either `APPROVED` or `REJECTED`.
- A parking permit can only be issued when the related application has an `APPROVED` decision.
- `permit_number` must be unique.
- `valid_until` must not be earlier than `valid_from`.

The REST API must return consistent error responses:

- `400 Bad Request` when input data is missing or invalid.
- `404 Not Found` when the requested permit application does not exist.
- `409 Conflict` when the requested operation conflicts with the current state, for example when attempting to issue a parking permit without an `APPROVED` decision.

## API

| Method + path | Does | Failure answers |
| --- | --- | --- |
| `GET /api/v1/permit-applications` | paged list; optional filters: `status`, `applicationDateFrom`, `applicationDateTo`; pagination and sorting as specified below; answers **200** | 400 invalid filter, pagination, or sort value |
| `POST /api/v1/permit-applications` | create an application using `personal_code`, `application_date` and optional `reason`; answers **201 + Location** identifying the created application | 400 validation |
| `GET /api/v1/permit-applications/{id}` | retrieve an application's details, including its decision and any issued permit | 404 application not found |
| `POST /api/v1/permit-applications/{id}/decision` | record the application's single decision using `decision_date`, `decision_type` and optional `decision_reason`; answers **201 + Location** identifying the application's details | 400 validation · 404 application not found · 409 decision already recorded |
| `POST /api/v1/permit-applications/{id}/permits` | issue a permit using `valid_from` and `valid_until`; the system generates the unique `permit_number`; answers **201 + Location** identifying the created permit | 400 validation · 404 application not found · 409 application is not approved or already has a permit |
| `GET /api/v1/parking-permits/{id}` | retrieve one issued permit, including its permit number, validity period and status | 404 permit not found |

Every error response uses `application/problem+json`.

The API uses URL-path versioning; the current version is `/api/v1`.

The application list response is an object containing a `data` array of application records and a `meta` object:

```json
{
  "data": [],
  "meta": {
    "limit": 10,
    "offset": 0,
    "total": 0
  }
}
```

Each application record contains `id`, `personal_code`, `application_date`, and `status`, and includes `reason` when present. `meta.total` is the number of records matching the filters before pagination.

- `limit` defaults to `10`; it must be an integer from `1` through `100`.
- `offset` defaults to `0` and must be a non-negative integer.
- `sort` accepts `applicationDate` or `status`; a field without a prefix sorts ascending, and prefixing it with `-` sorts descending. If omitted, results are ordered by `applicationDate` descending, then `id` ascending.
- Invalid pagination or sort values return `400 Bad Request`.

The decision endpoint does not expose a separate decision resource. Its `Location` header points to the existing application details resource, `GET /api/v1/permit-applications/{id}`.

## UI and business requirements

### UI requirements

**UI-01 · Create application**

The system shall provide the municipal officer with a form for creating a permit application. The form shall allow the officer to enter the person's personal identity code, application date and optional reason.

**UI-02 · Application details**

The system shall provide the municipal officer with a view of a permit application's details and current status. Current personal information retrieved through the registry adapter may be displayed when available but shall not be stored locally by the Parking Register.

**UI-03 · Record decision**

The system shall allow the municipal officer to record an `APPROVED` or `REJECTED` decision for an application. The interface shall support the decision date and optional decision reason defined in the data model.

**UI-04 · Issue permit**

The system shall provide the municipal officer with an action for issuing a parking permit for an application with an `APPROVED` decision.

**UI-05 · Prevent invalid permit issuance**

If the municipal officer attempts to issue a parking permit for an application without an `APPROVED` decision, the permit shall not be created and the interface shall display a clear error message.

**UI-06 · Permit details**

The system shall display an issued permit's unique permit number, validity period and current status.

### Existing business rules

The UI shall respect the business rules already defined by the User Story and specification:

- A permit decision is either `APPROVED` or `REJECTED`.
- A parking permit may only be issued for an application with an `APPROVED` decision.
- The permit is linked to a person rather than to a specific vehicle.
- Current personal information such as name and address is retrieved through the registry adapter when needed and is not stored locally by the Parking Register.

### Agreed business flow and scope

The application submission channel is outside the scope of the Parking Register. The system assumes that the municipality has already received the application through an external channel.

The agreed business flow is:

Application received externally → Municipal officer creates the application → Officer records the decision → If the decision is `APPROVED`, the officer issues the parking permit.

The delivery of the issued permit to the applicant, whether physical or digital, is outside the scope of the Parking Register.

The following status behavior is in scope:

- `permit_application.status`: `RECEIVED` → `DECIDED`
- The decision outcome is stored in `permit_decision.decision_type` as `APPROVED` or `REJECTED`.
- `parking_permit.status` is set to `ACTIVE` when the permit is issued.

Automatic expiry processing and permit revocation are out of scope. The permit's `valid_until` date is recorded, but this specification does not define transitions to `EXPIRED` or `REVOKED` or a revocation workflow.

The parking permit is linked to a person, not to a specific vehicle.

## Relevant non-functional requirements

The following TEHIK non-functional requirements are relevant to the Accessible Parking Permit Register:

- **NFR-01 · API versioning:** The REST API shall use URL-path versioning, with the current version at `/api/v1`, in accordance with TEHIK requirement 3.3.
- **NFR-02 · Database migrations:** Database changes shall be automated and versioned using Liquibase, in accordance with TEHIK requirement 3.7.
- **NFR-03 · Accessibility:** The web user interface shall comply with WCAG 2.2 Level AA, in accordance with TEHIK requirement 4.1.
- **NFR-04 · Error handling:** User-facing errors shall be presented as clear, user-friendly error messages, in accordance with TEHIK requirement 4.6.
- **NFR-05 · Database relationships:** Relational references shall use foreign keys and each table shall have a defined primary key, in accordance with TEHIK requirements 6.3 and 6.6.
- **NFR-06 · Personal data and logging:** System logs shall not contain direct personal data, in accordance with TEHIK requirement 8.12.
- **NFR-07 · Secure communication:** Data transmission shall use appropriately protected channels, in accordance with TEHIK requirement 9.6.



## Business tests

1. **An application can be created with a reason.**

   Given an 11-digit personal identity code and application date  
   When the municipal officer creates an application with a reason  
   Then the application is stored with that reason and a `RECEIVED` status

2. **An application can be created without a reason.**

   Given an 11-digit personal identity code and application date  
   When the municipal officer creates an application without a reason  
   Then the application is stored without a reason and with a `RECEIVED` status

3. **An application with an invalid personal code is rejected.**

   Given a personal code that is not exactly 11 digits  
   When the municipal officer creates an application  
   Then the request is rejected with `400 Bad Request` and no application is stored

4. **Recording a decision changes the application status.**

   Given a permit application with a `RECEIVED` status  
   When the municipal officer records an `APPROVED` decision  
   Then the decision is stored and the application status becomes `DECIDED`

5. **An application cannot receive a second decision.**

   Given a permit application that already has a decision  
   When the municipal officer attempts to record another decision  
   Then the request is rejected with `409 Conflict` and the original decision remains unchanged

6. **A parking permit can be issued for an approved application.**

   Given a person has a permit application with an `APPROVED` decision  
   When the municipal officer issues a parking permit for that person  
   Then a parking permit is created for the person with a unique permit number, validity period, and status

7. **A parking permit cannot be issued without an approved decision.**

   Given a person has a permit application without an `APPROVED` decision  
   When the municipal officer tries to issue a parking permit for that person  
   Then the request is rejected and no parking permit is created

8. **An application cannot receive a second parking permit.**

   Given a permit application that already has a parking permit  
   When the municipal officer attempts to issue another permit for that application  
   Then the request is rejected with `409 Conflict` and the original permit remains unchanged
