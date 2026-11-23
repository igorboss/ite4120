# ITE4120 — Parma Lost and Found Registry

A course project built on the ITE4120 e-government template. The application
provides a municipal Lost and Found Registry for Parma: citizens can create
LOST and FOUND reports, search and filter existing reports, and consult
collection points through an external-registry adapter.

The repository retains the course's Animals Register as a worked example. The
student component is `lostfound`, implemented as its own Spring package,
PostgreSQL schema, Liquibase changelog and React pages.

Everything Helex arrives as **published packages** — there is no platform source
checkout here and none is needed.

| Layer | Where | Session |
| --- | --- | --- |
| Specification | `docs/src/` (mdBook) | S1 |
| Data — PostgreSQL + Liquibase | `backend/src/main/resources/lostfound/db/` | S2 |
| API — Spring Boot + springdoc | `backend/src/main/java/.../lostfound/` | S3 |
| Registry adapter (mock + X-Road boundary) | `.../lostfound/registry/` | S3 |
| UI — React + @helex/ui | `frontend/src/pages/lostfound/` | S4 |

The UI wears the **TalTech theme** — the `taltech` pack from
`helex-solutions/helex-extensions`, registered in `frontend/src/theme/taltech.ts`
and selected by `VITE_THEME` in `frontend/.env` (the @helex/ui built-ins
`helex`, `tedi`, `matrix` work there too).

## Run it

First time on this machine? Install the toolchain and the GitHub Packages
token first — [§ Tools](#tools), at the end of this file. Then:

```bash
# 1. database + backend  (first run downloads dependencies — takes a few minutes)
./scripts/run-backend.sh          # Windows: .\scripts\run-backend.ps1

# 2. frontend, in a second terminal  (npm needs the token too — § Tools › GitHub token)
cd frontend && npm install && cd ..
./scripts/run-frontend.sh         # Windows: .\scripts\run-frontend.ps1
```

| What | Where |
| --- | --- |
| The application | http://localhost:18640 (Lost and Found) |
| Swagger UI — the live API contract | http://localhost:18440/swagger-ui.html |
| Lost and Found API | http://localhost:18440/swagger-ui.html |
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

The Lost and Found business tests are in `LostFoundBusinessRulesTest`; the original
Animals tests remain as the course template examples. Testcontainers also verifies
that the Liquibase changelogs run from an empty PostgreSQL database.

The same tests, the frontend typecheck and build, and the changeset gate run on
every pull request as the **Verify** checks (`.github/workflows/verify.yml`);
they need nothing from you beyond the repository itself. A copy of this
repository whose Actions token cannot read the Maven mirror adds a `GPR_TOKEN`
repository secret (the same classic token as in § Tools).

## Project documentation

- User story: `docs/src/user-stories/LOSTFOUND-US-001.md`
- Specification: `docs/src/specifications/LOSTFOUND.01-lost-and-found.md`
- Presentation and demo guide: `docs/src/presentations/lost-found-demo.md`
- Repository and architecture rules: `AGENTS.md`

## Reading order

`docs/src/README-docs.md` → the animals story and spec in `docs/src/` →
the three manuals in `docs/src/manuals/` (database, REST API, frontend) →
`docs/src/liquibase-guide.md` → `docs/src/development-workflow.md`. Or serve the
book with live reload: `scripts/run-docs` (`.sh` / `.ps1`).

## Tools

What the scripts and builds in this repository actually call, and why:

| Tool | Version | Used for |
| --- | --- | --- |
| **Git** | any recent | clone, branch, PR — your contribution is the history under your name (gate B) |
| **Docker Desktop** (or a compatible engine with `docker compose`) | running | PostgreSQL locally (`scripts/run-backend`, `reset-db`, `psql`); a throwaway PostgreSQL in the tests (Testcontainers) |
| **JDK 25** | 25 | the Gradle wrapper runs on it *and* compiles with it — nothing downloads a JDK for you (no toolchain resolver is configured), so it must be installed |
| **Gradle** | 9.4 | provided by the wrapper: `backend/gradlew` downloads the pinned version on first use. Nothing to install; a second, system-wide Gradle only invites version mix-ups |
| **Node.js** + npm | 20+ | the Vite frontend (`scripts/run-frontend`, `npm ci`, `npx tsc -b`) |
| **Python 3** | 3.x | `scripts/check-changesets` — the formatted-SQL grammar gate (rule 6 in `AGENTS.md`) |
| **GitHub account** + token with `read:packages` | — | the Helex packages live in GitHub Packages, which refuses anonymous reads |
| GitHub CLI `gh` | optional | pull requests from the terminal; not needed for the token |
| mdBook | optional | serves `docs/` as a book — `scripts/run-docs` |

No local `psql` needed — `scripts/psql` runs it inside the container.

Docker engines other than Docker Desktop (Colima, Rancher Desktop, Podman) work
for running the app, but the tests need to be told where the socket is — with
Colima: `export DOCKER_HOST=unix://$HOME/.colima/default/docker.sock
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock` before
`./gradlew test`.

### Install with a package manager

One script per platform, idempotent, safe to re-run. Each installs the
package manager itself if it is missing, then the rows above, then prints a
version check:

```bash
# macOS — Homebrew
./scripts/setup-tools.sh
```

```powershell
# Windows — Chocolatey, in PowerShell run as Administrator
.\scripts\setup-tools.ps1
```

By hand, the same thing:

| | macOS (Homebrew) | Windows (Chocolatey) |
| --- | --- | --- |
| Git, GitHub CLI | `brew install git gh` | `choco install -y git gh` |
| JDK 25 | `brew install openjdk@25` — keg-only: register it with `sudo ln -sfn "$(brew --prefix openjdk@25)/libexec/openjdk.jdk" /Library/Java/JavaVirtualMachines/openjdk-25.jdk` | `choco install -y temurin25` (sets `JAVA_HOME` and `PATH`) |
| Node.js + npm | `brew install node` | `choco install -y nodejs-lts` |
| Python 3 | `brew install python` | `choco install -y python` |
| Docker Desktop | `brew install --cask docker-desktop` | `choco install -y docker-desktop` — needs WSL 2: `wsl --install` once, reboot |
| mdBook (optional) | `brew install mdbook` | no Chocolatey package — see the [mdBook installation guide](https://rust-lang.github.io/mdBook/guide/installation.html) |

Windows notes: run the repository's PowerShell scripts as `.\scripts\name.ps1`;
if PowerShell refuses to run scripts, `Set-ExecutionPolicy -Scope CurrentUser
RemoteSigned` once. Docker Desktop must be started once by hand after install
(it finishes the WSL 2 setup on first start). winget and Scoop carry the same
packages if you prefer them.

### GitHub token

The one step nothing works without: the Helex packages live in GitHub
Packages, which refuses anonymous reads.

**Create the token** at
**[github.com/settings/tokens/new — pre-filled for this project](https://github.com/settings/tokens/new?description=ITE4120+Helex+packages+read-only&scopes=read:packages)**
— the form opens with exactly the one scope this project needs; pick an
expiration, press *Generate token*, copy it once. From a terminal:

```bash
open "https://github.com/settings/tokens/new?description=ITE4120+Helex+packages+read-only&scopes=read:packages"
```

(Windows: paste the URL into the browser, or `start ""` instead of `open`.)
It must be a **classic** token: GitHub's Maven and npm registries do not
accept fine-grained tokens. `read:packages` alone is the minimum privilege —
it can download packages your account can see, and nothing else.

**Both package managers need it.** Gradle reads it from
`~/.gradle/gradle.properties`; npm reads it from its global config — and
GitHub's npm registry refuses anonymous reads **even for public packages**,
so `npm install` in `frontend/` fails with a 401 until this has run once:

```bash
npm config set //npm.pkg.github.com/:_authToken=ghp_YOUR_TOKEN --global
```

**Wire it in** — one paste, silent prompt, no token in your shell history
(macOS/Linux; on Windows edit the two files by hand as shown below):

```bash
read -s TOKEN && printf 'gpr.user=%s\ngpr.key=%s\n' "YOUR_GITHUB_USERNAME" "$TOKEN" >> ~/.gradle/gradle.properties && npm config set "//npm.pkg.github.com/:_authToken=$TOKEN" --global && unset TOKEN
```

What that writes, if you prefer to do it by hand:

```properties
# ~/.gradle/gradle.properties  (backend)
#   Windows: %USERPROFILE%\.gradle\gradle.properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=ghp_YOUR_TOKEN
```

```bash
# npm (frontend) — one command, same on Windows:
npm config set //npm.pkg.github.com/:_authToken=ghp_YOUR_TOKEN --global
```

Missing or wrong token looks like this: Gradle says *cannot resolve
org.helex.emr:…*, npm says *401*. Fix the credentials first — never vendor
jars. No other access is needed: the backend's Helex jars resolve from THIS
repository's own Maven registry (mirrored by the lecturer with
`scripts/mirror-packages.sh`), and the `@helex-solutions` npm packages are
public — GitHub's npm registry still wants the token to read them, but no
extra permission. A correct token that still gets 401/403 means the token is
expired or you have not accepted the invitation to this repository; ask the
lecturer.
