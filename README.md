# ITE4120 — Animals Register · Course Template

A working e-government-shaped application, built the way the Estonian public
sector builds them — and the template your group extends with **your own
component**. The worked example is the **animals register**: three tables under
Liquibase, a documented REST API, an owner lookup through a registry adapter
(mock locally, X-Road-shaped for production), and a React UI on the Helex
component library.

Everything Helex arrives as **published packages** — there is no platform source
checkout here and none is needed.

| Layer | Where | Session |
| --- | --- | --- |
| Specification | `docs/src/` (mdBook) | S1 |
| Data — PostgreSQL + Liquibase | `backend/src/main/resources/animals/db/` | S2 |
| API — Spring Boot + springdoc | `backend/src/main/java/.../animals/` | S3 |
| Registry adapter (mock + X-Road) | `.../animals/ownerregistry/` | S3 |
| UI — React + @helex/ui | `frontend/src/` | S4 |

The UI wears the **TalTech theme** — the `taltech` pack from
`helex-solutions/helex-extensions`, registered in `frontend/src/theme/taltech.ts`
and selected by `VITE_THEME` in `frontend/.env` (the @helex/ui built-ins
`helex`, `tedi`, `matrix` work there too).

## Prerequisites

1. **Docker Desktop** (or compatible) — running.
2. **A GitHub account** and a token with `read:packages`. The Helex packages
   live in GitHub Packages, which refuses anonymous reads — this is the one
   step nothing works without.

   **Create the token** (opens the form pre-filled with exactly the one scope
   this project needs — pick an expiration, press *Generate token*, copy it
   once):

   ```bash
   open "https://github.com/settings/tokens/new?description=ITE4120+Helex+packages+read-only&scopes=read:packages"
   ```

   (Windows: paste the URL into the browser, or `start ""` instead of `open`.)
   It must be a **classic** token: GitHub's Maven and npm registries do not
   accept fine-grained tokens. `read:packages` alone is the minimum privilege —
   it can download packages your account can see, and nothing else.

   **Wire it in** — one paste, silent prompt, no token in your shell history
   (macOS/Linux; on Windows edit the two files by hand as shown below):

   ```bash
   read -s TOKEN && printf 'gpr.user=%s\ngpr.key=%s\n' "YOUR_GITHUB_USERNAME" "$TOKEN" >> ~/.gradle/gradle.properties && npm config set "//npm.pkg.github.com/:_authToken=$TOKEN" --global && unset TOKEN
   ```

   What that writes, if you prefer to do it by hand:

   ```properties
   # ~/.gradle/gradle.properties  (backend)
   gpr.user=YOUR_GITHUB_USERNAME
   gpr.key=ghp_YOUR_TOKEN
   ```

   ```bash
   # npm (frontend) — one command:
   npm config set //npm.pkg.github.com/:_authToken=ghp_YOUR_TOKEN --global
   ```

   The token unlocks only packages your account can access — if `gradle` or
   `npm install` answers 401/403 with a correct token, you are missing access
   to the `helex-solutions` packages; ask the lecturer.
3. **Java is downloaded for you** — the Gradle toolchain fetches JDK 25 on first
   build. **Node 20+** for the frontend.

## Run it

```bash
# 1. database + backend  (first run downloads dependencies — takes a few minutes)
./scripts/run-backend.sh          # Windows: .\scripts\run-backend.ps1

# 2. frontend, in a second terminal
cd frontend && npm install && cd ..
./scripts/run-frontend.sh         # Windows: .\scripts\run-frontend.ps1
```

| What | Where |
| --- | --- |
| The application | http://localhost:18640 |
| Swagger UI — the live API contract | http://localhost:18440/swagger-ui.html |
| The imitated population registry | http://localhost:18440/mock-registry/persons/38102130265 |
| PostgreSQL | localhost:18520 · `scripts/psql` for a shell |
| The documentation book | `scripts/run-docs` → http://localhost:18740 (needs [mdBook](https://rust-lang.github.io/mdBook/guide/installation.html), no Docker) |

Signed in automatically as a mock user; the API accepts
`Authorization: Bearer <any-username>` in local mode. The database is
disposable: `scripts/reset-db` destroys and recreates it, and Liquibase
rebuilds the schema on the next backend start.

## Tests

```bash
cd backend && ./gradlew test      # business tests, against a throwaway PostgreSQL
```

The two tests in `AnimalBusinessRulesIT` are the specification's business tests,
executable — and they also prove the migrations run clean from empty.

## Your component

1. Copy `docs/_templates/` → write your user story and specification (different
   domain from animals!).
2. New package `ee.taltech.ite4120.<yours>`, new schema, new changelog folder,
   one include added to the master changelog.
3. Follow the animals example file-by-file; the shape is the deliverable.
4. Rules of the road: `AGENTS.md` (for your agent — and for you).

## Reading order

`docs/src/README-docs.md` → the animals story and spec in `docs/src/` →
`docs/src/liquibase-guide.md` → `docs/src/development-workflow.md`. Or serve the
book with live reload: `scripts/run-docs` (`.sh` / `.ps1`).
