# Work diary — Helena

## 2026-11-dd

**Time spent:** 

**Work completed:**
- 

**AI assistance:**
- 

**Corrections and decisions:**
- 

## 2026-11-15

**Time spent:** 3,5 hours (incl. 30 min team call)

**Work completed:**
- Made a file-by-file task list for the whole component, grouped by person
- Team call (30 min): agreed who takes the unassigned tasks — Heiki: catalogue screen and routes; Helena: demo data, docs and page titles
- Reviewed the book and copy tables against the spec; fixed the book table (added the `sys_*` columns, ISBN unique among active rows only)
- Set up branch protection on `main` (pull request with 1 approval required) and enabled the automated Verify checks in GitHub Actions
- Built the books REST API: catalogue search, one book, register, remove, one copy, update lending, and the borrower name lookup from the mock registry (pull request #5)
- Updated the specification to match the API: two GET endpoints, paging, ISBN without hyphens, borrower lookup and due-date rules
- Tested all API endpoints in Swagger UI (GET, POST, PUT, DELETE): registering, listing, lending, returning and removing books, re-registering a removed book's ISBN, validation errors (400), conflicts (409) and authorization (401)
- Added demo data: 4 books and 7 copies, one per status (pull request #6)
- Prepared the column spacing fix in `02-copy.sql` for Heiki to review (pull request #7)
- Made a step-by-step API test guide (Swagger test steps with expected results)
- Added the books user story and spec to the docs table of contents (`SUMMARY.md`)
- Marked the specification and user story as Confirmed
- Updated the work plan in `TEAM.md` with the new task split
- Added the frontend link to the API (`booksApi` in `frontend/src/api.ts`) so the forms can call the endpoints (pull request #8)
- Added automated business tests 1, 2, 6 and 10 in `BookBusinessRulesIT`; they pass in the GitHub Verify check (pull request #9)

**AI assistance:**
- Used AI to write the API, demo data and spec changes, following the teacher's animals example file by file
- Used AI to check the code and SQL (compile check, SQL syntax, changeset grammar) and to cross-check all 12 business tests against the code
- Used AI to explain test results in Swagger and how GitHub rulesets and checks work

**Corrections and decisions:**
- Edited the column spacing in `02-copy.sql` to match the teacher's SQL format requirement (`check-changesets`), as the failing check blocked every pull request; Heiki to review and approve
- Added `GET /api/books/{id}` and `GET /api/copies/{id}`, and decided that a due date with a non-BORROWED status is refused
- GitHub cannot exclude single files from required approvals, so diary and `TEAM.md` updates go in the same pull request as the related work

## 2026-11-08

**Time spent:** 4 hours (incl. 1 h team call)

**Work completed:**
- Forked the teacher's repository (`ite4120-books`) and moved our earlier docs and commit history into it; archived the old repository
- Renamed our old `readme.md` to `TEAM.md` and updated the project requirements
- Made a work plan with steps, owners and checks, reviewed it on team call and added it to `TEAM.md` 
- Updated `README.md` to describe our library books component
- Prepared setup instructions for Windows + VS Code and shared with the team and walked through the steps on team call
- Planned the table folder structure under `backend/src/main/resources/books/`
- Team call (1 h): agreed on next steps and split the work — Mari: book table and register form; Heiki: copy table and lending form; Helena: APIs (GET, POST, PUT, DELETE)
- Set up the development environment on Windows: ran the tool setup script, created the GitHub token, configured Gradle and npm
- Started the backend and frontend; the template app runs on localhost

**AI assistance:**
- Used AI to compare our spec and user story with the teacher's example and the assessment criteria
- Used AI to merge the old repository's history into the fork and open pull requests
- Used AI to explain the data model: title vs copy level, primary key vs unique ISBN, and how ESTER (Sierra) structures bibliographic and item records

**Corrections and decisions:**
- Decided to fork the teacher's repository so he can see our work, while keeping all earlier commits
- User story to be renamed to `BOOKS-US-001.md` so it matches the spec
- Docker Desktop install from the setup script failed (error 1603) because Docker was already installed — kept the existing Docker
- Frontend failed with 401 at first because the npm token was not set; fixed by setting the token with `npm config set`

## 2026-11-01

**Time spent:** 2 hours

**Work completed:**
- Added spec file
- Created a branch to commit to github repo

**AI assistance:**
- Asked AI to create a spec file based on brainstorming notes
- Asked AI help on how to protect main branch

**Corrections and decisions:**
- User Story moved to user story folder and needs some updates too after spec file

## 2026-10-25

**Time spent:** 1 hour

**Work completed:**
- Brainstormed on user story topics on team call
- Created initial draft for user story and specification and distributed workload and defined next steps

## 2026-10-21

**Time spent:** 1.5 hours

**Work completed:**
- Created the group GitHub repository and added initial project information.
- Reviewed the course notes, syllabus, README, development workflow, and assessment criteria.

**AI assistance:**
- Asked AI to explain the course project requirements, workflow, and repository setup.
- Asked AI for examples of a user story and a work-diary entry.

**Corrections and decisions:**
- Corrected the diary plan after checking the development workflow: each student should keep a separate diary.
- Clarified that the diary should record actual work, time spent, AI assistance, and corrections.
