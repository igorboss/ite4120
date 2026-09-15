# Agent contract — ITE4120 animals-register template

Instructions for AI coding agents working in this repository. Humans: this is
also your onboarding — the rules are identical.

## What this is

A course template: a working Spring Boot + React application consuming the Helex
platform as **published libraries** (`org.helex.emr:*` from GitHub Packages,
`@helex/ui` and friends from GitHub npm). The worked example component is the
**animals register** (`ee.taltech.ite4120.animals`); each student group adds its
own component in a different domain, following the same shape.

## Process rules (non-negotiable)

1. **Spec first.** No implementation before a confirmed specification in
   `docs/src/specifications/`. When asked to build something unspecified, ask for the
   spec — or draft one and stop for confirmation.
2. **Tests second.** Business tests from the spec become executable tests before
   the implementation. Never write a test to match code you just wrote.
3. **Update the spec in the same PR** when the implementation settles differently.
4. **Never commit to main.** Branch, PR, review.
5. **Never edit a released Liquibase changeset.** Append a new one — see
   `docs/src/liquibase-guide.md`.
6. **Formatted-SQL grammar.** `--` lines belong to Liquibase (directives and the
   bare `--` terminator closing every changeset); prose is a single-line
   `--comment` plus a `/* */` block; body comments that start a line are
   `/* */` blocks; datatypes align at column 25; business-table indexes carry
   `where (sys_status = 'A')`. Every file under `animals/db/changelog/` is the
   example.

## Architecture conventions

- One component = one Java package under `ee.taltech.ite4120.<component>` + one
  schema + one changelog folder + pages under `frontend/src/pages/`.
- Layers: controller (HTTP only) → service (business rules) → repository
  (extends `BaseRepository`, uses `SqlBuilder`/`PgBeanProcessor`). Business
  logic in a controller is a defect.
- Errors: `application/problem+json` everywhere, via `ApiErrorHandler`. 4xx =
  caller's fault, 5xx = ours. Throw the commons exceptions
  (`NotFoundException`, `ConflictException`, `ApiClientException`).
- External registries go behind an adapter interface with schema-faithful wire
  records (Estonian field names stay Estonian) and a single mapper — see
  `ownerregistry/`. Never store data a registry owns.
- IDs from `core.seq_id`; `sys_*` columns on business tables; soft delete via
  `sys_status`; identifiers (isikukood, codes) are **strings, never numbers**.
- UI: every input is an existing `@helex/ui` or antd component. Avoid the
  calendar family (`AppCalendar`, `ResourceForm`) — its stylesheet is not
  reachable from the published package.

## Known pitfalls (earned the hard way)

- Missing `GITHUB_TOKEN`/`gpr.key` → Gradle "cannot resolve org.helex.emr" and
  npm 401. Fix credentials first; do not vendor jars.
- Spring dispatches 404s to `/error`; the security chain must permit the ERROR
  dispatcher or every 404 becomes a 401 (already configured — do not remove).
- The frontend MockAuthProvider probes `/api/uma/auth/mock-users` and
  `/api/uma/userinfo` pre-auth; `MockUmaController` answers them. Removing it
  brings back error toasts on every page load.
- Testcontainers runs as DB user `test` — `application-test.yml` overrides the
  Liquibase grant parameters. Your component needs no change there.
- Two Reacts = "Invalid hook call": keep the `overrides` in
  `frontend/package.json` and the `dedupe` list in `vite.config.ts`.
- Bumping `helexCommonsVersion` can change the checksums of the platform's own
  `core-db` changesets, and the backend then refuses to start against an old
  local database. That is Liquibase working as designed — run `scripts/reset-db`
  (the database is disposable) and it replays clean. Testcontainers tests are
  unaffected: they always start from empty.
- Testcontainers 2.x moved `PostgreSQLContainer` to
  `org.testcontainers.postgresql` (non-generic); the old
  `org.testcontainers.containers` import is a deprecated shim.
- `useAppNotification()` from the published `@helex/ui` (≤ 1.0.37) returns a
  **new object on every render**. Never list its result in a hook dependency
  array — an effect that does re-runs after every state update, and a
  fetch-then-setState page polls the backend forever. Hold it in a ref, as
  `AnimalList.tsx` and `AnimalCreate.tsx` do. Fixed upstream in
  helex-solutions/emr-repo#2919; the workaround can go once the pin moves past
  that release.

## Verification before any PR

```
backend:  ./gradlew test          # includes migrations-from-empty via Testcontainers
frontend: npx tsc -b              # typecheck
db:       scripts/check-changesets  # formatted-SQL grammar + layout (rule 6), fails closed
manual:   scripts/run-backend + run-frontend → the flow you touched, in the browser
```
