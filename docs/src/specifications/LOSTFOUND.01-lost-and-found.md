# Lost and Found Registry — component specification

**Status:** Confirmed for implementation
**Component:** `lostfound`
**Context:** Municipality of Parma, Italy

## 1. Purpose and scope

The Lost and Found Registry allows citizens to publish reports about items they have lost or found in Parma. Users can browse and filter reports by report type, category and location. The application also displays municipal collection points obtained through an external registry adapter.

The application is a course prototype. It does not provide citizen accounts, private messaging, automatic item matching, image uploads or physical collection management.

## 2. User stories

- As a citizen, I can submit a LOST or FOUND report so that an item can be registered.
- As a citizen, I can search reports by item name or description.
- As a citizen, I can filter reports by type, category and location.
- As a citizen, I can view municipal collection points returned by the external registry.

## 3. Data model

The component owns three tables in schema `lostfound`:

| Table | Purpose | Key relationships |
|---|---|---|
| `category` | Controlled list of item categories | Natural primary key `code` |
| `location` | Registered locations in Parma | Natural primary key `code` |
| `item_report` | Citizen-submitted reports | `id` from `core.seq_id`; FKs to category and location |

`item_report.report_type` is constrained to `LOST` or `FOUND`. Business rows carry platform `sys_*` metadata and use soft-delete status. The application never writes metadata columns.

## 4. Business rules

1. Report type must be `LOST` or `FOUND`.
2. Item name and description are mandatory; item name is at most 160 characters and description at most 2,000 characters.
3. Report date is mandatory and cannot be in the future.
4. Category and location must exist in the component reference tables.
5. Search filters are optional and can be combined.
6. If the external collection-point registry is unavailable, report creation and search continue to work. Only the collection-point endpoint returns a gateway error.

## 5. REST API

All endpoints are under `/api/lost-found` and use the platform's `application/problem+json` error handling.

| Method | Path | Purpose |
|---|---|---|
| GET | `/reports` | Paginated search; supports `textContains`, `reportType`, `categoryCode`, `locationCode`, `limit`, `offset`, `sort` |
| GET | `/reports/{id}` | Retrieve one active report |
| POST | `/reports` | Create a report; returns `201 Created` and `Location` |
| GET | `/categories` | Category reference data |
| GET | `/locations` | Parma location reference data |
| GET | `/collection-points` | External registry adapter result |

Creation request example:

```json
{
  "reportType": "FOUND",
  "itemName": "Black wallet",
  "description": "Black leather wallet found near the main entrance.",
  "reportDate": "2026-11-20",
  "categoryCode": "BAGS",
  "locationCode": "PARCO_DUCALE"
}
```

Invalid request fields and invalid reference codes produce a client error. A future date is rejected. An unavailable collection-point service produces `502 Bad Gateway` without affecting core report operations.

## 6. External registry and X-Road boundary

The application depends on `CollectionPointRegistryAdapter`, not on a transport implementation. The `local` and `test` profiles use a deterministic mock adapter so the flow can be demonstrated without external infrastructure. The production-profile adapter is the X-Road integration boundary; it intentionally fails closed with a clear configuration error until the municipality's actual X-Road service identifier, WSDL and Security Server endpoint are supplied. No real X-Road service is claimed to be connected by this prototype.

## 7. User interface

The `/lost-found` page presents the report list, text search, type/category/location filters and collection-point panel. `/lost-found/new` provides the report creation form. Both pages use the course's Helex UI components and Ant Design inputs.

## 8. Business tests

- A future report date is rejected.
- A category code not present in the reference table is rejected.
- A location code not present in the reference table is rejected.
- A valid report is persisted and can be retrieved.
- Report type, category, location and text filters can be combined.
- Failure of the external collection-point registry does not prevent report creation or search.
