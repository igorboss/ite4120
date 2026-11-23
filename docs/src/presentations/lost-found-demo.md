# Lost and Found Registry — presentation and demo guide

## Suggested presentation structure (7–9 minutes)

1. **Problem and users (1 min):** citizens need a simple way to report lost/found property in Parma and search existing reports.
2. **Scope (1 min):** explain the two report types, controlled categories and locations, search filters, and external collection-point lookup. Mention excluded features.
3. **Architecture (2 min):** React/Helex UI → REST controller → service/business rules → repository → PostgreSQL. Liquibase owns schema creation. The collection-point registry is isolated behind an adapter interface.
4. **Data model (1 min):** show `category`, `location`, and `item_report`; explain the two foreign keys, `core.seq_id`, and platform `sys_*` metadata.
5. **Live demo (2 min):** create a FOUND report; return to the list; search by text; filter by report type/category/location; show collection points.
6. **Validation and resilience (1 min):** demonstrate a future date or missing field; explain that a registry outage affects only collection-point display, not report creation/search.
7. **Limitations and next steps (1 min):** real X-Road deployment needs the provider's WSDL, service identifiers, namespace and a reachable Security Server. The included X-Road adapter is an integration skeleton; the local profile uses a deterministic mock registry.

## Run locally

From the repository root:

```bash
scripts/run-backend.sh
scripts/run-frontend.sh
```

Open `http://localhost:18640/` (Vite may choose the next free port if 18640 is occupied). The local backend uses the mock authentication and mock collection-point registry.

## API smoke test

Create a report:

```bash
curl -i -X POST http://localhost:18440/api/lost-found/reports \
  -H 'Authorization: Bearer superadmin' -H 'Content-Type: application/json' \
  -d '{"reportType":"FOUND","itemName":"Black wallet","description":"Black leather wallet found near the park entrance.","reportDate":"2026-11-20","categoryCode":"BAGS","locationCode":"PARCO_DUCALE"}'
```

List reports:

```bash
curl -H 'Authorization: Bearer superadmin' 'http://localhost:18440/api/lost-found/reports?limit=50'
```

Collection points:

```bash
curl -H 'Authorization: Bearer superadmin' http://localhost:18440/api/lost-found/collection-points
```

## Verification commands

```bash
cd backend && ./gradlew test
cd frontend && npx tsc -b
cd .. && scripts/check-changesets.sh
```

The X-Road adapter is deliberately not represented as a live connection. Before using the `xroad` Spring profile, replace the illustrative wire schema and service identifiers with the external provider's published contract and configure a reachable Security Server.
