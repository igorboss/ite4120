# PARKING-US-001 · Issue an accessible parking permit

> **PARKING-US-001** · state **Draft** · traces to [PARKING.01](../specifications/PARKING.01-accessible-parking-permit-register.md)

**As** a municipal officer  
**I want** to issue an accessible parking permit to an eligible person  
**so that** the person's permit can be registered and managed digitally.

## Context

The accessible parking permit is linked to a person, not to a specific vehicle. The register stores the person's personal identity code and uses the Population Register through X-Road to retrieve current identity information when needed. Personal details such as the person's name and address are not stored locally. The Population Register remains the source of truth for personal data.

An officer processes a permit application, records a decision and, when the application is approved, issues an accessible parking permit.

## Acceptance criteria

- A permit application can be created using an 11-digit personal identity code.
- The person's current identity information is retrieved from the Population Register through X-Road and is not stored locally.
- An application can receive an approved or rejected decision.
- A parking permit can be issued only when the application has an approved decision.
- An attempt to issue a permit without an approved decision is rejected with a clear error message.
- An issued permit has a unique permit number, validity period and status.
- The permit is linked to the person, not to a specific vehicle.
