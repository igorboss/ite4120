# User story — Lost and Found Registry

**As a** citizen of Parma,
**I want** to report an item that I have lost or found and search existing reports,
**so that** lost property can be identified and returned to its owner.

## Acceptance criteria

1. A report has a type (`LOST` or `FOUND`), item name, description, date, category and location.
2. Required fields cannot be empty.
3. The report date cannot be in the future.
4. Category and location must be selected from the municipality's reference data.
5. Citizens can search by item name or description and filter by type, category and location.
6. Municipal collection points are displayed when the external registry is available.
7. If the external registry is unavailable, report submission and search remain available.

## Out of scope

Citizen accounts, private messaging, automatic matching, image uploads and physical collection management.
