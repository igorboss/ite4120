---
id: LIBRARY.01
title: Library lending
state: Proposed
traces-from: [LIBRARY-US-001]
source-refs: []
---

# LIBRARY.01 · Library lending

A second component in the template, in a different domain from the animals
register: three business tables joined by two foreign keys, an external registry
behind an adapter with a **fail-closed** policy, and three list pages with forms.
It follows ANIMALS.01 section by section; where it deliberately differs, it says
so.

## Description

Books, students and loans: register books and students, lend a copy of a book to
an enrolled student, return it, retire either. Whether a person is an enrolled
student is never stored — it is asked live from the education information system
through the enrolment-registry adapter (imitated in this course, X-Road-shaped in
production), at registration and at every loan. Excluded: reservations, fines,
branches, reminders, inventory.

## Data model  *(→ session 2, changesets 01–03)*

Schema `library`. IDs from `core.seq_id`; `sys_*` columns on every table,
trigger-managed; soft delete via `sys_status`. Identifiers (ISBN, student code,
isikukood) are **strings, never numbers**.

| Table | Field | Type | Rules |
| --- | --- | --- | --- |
| `library.book` | `id` PK | bigint | from `core.seq_id` |
| | `isbn` | text | required; 10 or 13 digits, no separators (check constraint); **unique among active rows** |
| | `title` | text | required, ≤255 |
| | `author` | text | required, ≤255 |
| | `published_year` | int | optional; **not in the future** |
| | `copies` | int | required; ≥ 1 (check constraint); **never below the number of open loans** |
| | `sys_*` | — | platform columns, trigger-managed |
| `library.student` | `id` PK | bigint | from `core.seq_id` |
| | `student_code` | text | required, ≤20; **unique among active rows** — matriculation numbers carry letters (`213482IAIB`) |
| | `first_name`, `last_name` | text | required, ≤100 each |
| | `isikukood` | text | required; exactly 11 digits (check constraint); the key the enrolment registry is asked about |
| | `email` | text | optional, ≤255 |
| | `sys_*` | — | platform columns, trigger-managed |
| `library.loan` | `id` PK | bigint | from `core.seq_id` |
| | `book_id` | bigint | required; FK → book |
| | `student_id` | bigint | required; FK → student |
| | `loaned_on` | date | required; not in the future |
| | `due_on` | date | required; **after** `loaned_on` (check constraint); default `loaned_on` + 28 days |
| | `returned_on` | date | null while the loan is open; when set, not before `loaned_on` (check constraint) |
| | `sys_*` | — | platform columns, trigger-managed |

Derived, never stored: a loan is **open** while `returned_on is null` and
**overdue** while open with `due_on` before today; a book's **available copies**
= `copies` − its open loans. Enrolment status is the registry's, never a column.

Indexes: `book_isbn_ukey` and `student_code_ukey`, unique
`where (sys_status = 'A')`; `loan_book_idx` and `loan_student_idx` on open loans,
`where (sys_status = 'A' and returned_on is null)`.

Changesets, in `backend/src/main/resources/library/db/changelog/library/`:
`library-schema.sql`, `01-book.sql`, `02-student.sql`, `03-loan.sql`,
`90-demo-data.sql` (context `demo`: three books, three students whose personal
codes the imitated registry knows, two open loans of which one is overdue). One
include added to the master changelog. Unlike animals there is no reference
table: every list the UI needs is a table of its own.

## API  *(→ session 3)*

All under `/api/library`. Every error is `application/problem+json`; all
endpoints require `Authorization: Bearer <user>` (mock auth); the contract is
live at `/swagger-ui.html`. Status vocabulary: **400** the request is wrong in
itself · **404** no such active row · **409** the current state forbids it ·
**502** the registry could not answer.

| Method + path | Does | Failure answers |
| --- | --- | --- |
| `GET /api/library/books` | paged search: `textContains` (title, author, ISBN), `available` (only books with a free copy), `limit`, `offset`, `sort` | — |
| `GET /api/library/books/{id}` | one active book, with `availableCopies` | 404 |
| `POST /api/library/books` | register; answers **201 + Location** | 400 validation · 409 duplicate ISBN |
| `PUT /api/library/books/{id}` | update; ISBN immutable | 400 · 404 · 409 copies below open loans |
| `DELETE /api/library/books/{id}` | retire (soft) — **204** | 404 · 409 has open loans |
| `GET /api/library/students` | paged search: `textContains` (name, code), `limit`, `offset`, `sort` | — |
| `GET /api/library/students/{id}` | one active student, with counts of open and overdue loans | 404 |
| `POST /api/library/students` | register — enrolment confirmed via the registry; **201 + Location** | 400 validation · 409 duplicate code · 409 not enrolled · 502 registry down |
| `PUT /api/library/students/{id}` | update name and e-mail; student code and isikukood immutable | 400 · 404 |
| `DELETE /api/library/students/{id}` | retire (soft) — **204** | 404 · 409 has open loans |
| `GET /api/library/students/{id}/enrolment` | live enrolment status via the registry adapter | 404 · 502 registry down |
| `GET /api/library/loans` | paged: `studentId`, `bookId`, `status` = `open` / `overdue` / `returned`, `limit`, `offset`, `sort` | — |
| `POST /api/library/loans` | lend: `{bookId, studentId, loanedOn?, dueOn?}` — enrolment re-checked; **201 + Location** | 400 validation, unknown book or student · 409 no free copy / five open loans / overdue loan / not enrolled · 502 registry down |
| `POST /api/library/loans/{id}/return` | return: `{returnedOn?}` (default today); answers the closed loan | 400 · 404 · 409 already returned |

Validation lives in three places, as in animals: Bean Validation for the shape
of the message (lengths, patterns, required fields), the service for
state-dependent rules (availability, limits, enrolment, uniqueness), the database
as the last line (check constraints, partial unique indexes, foreign keys).

**Failure policy** for the enrolment lookup: **fail closed** — when the registry
is unreachable, `POST /students` and `POST /loans` answer 502 and record nothing.
Rationale: enrolment is the precondition of lending, not decoration; a library
that lends while blind accrues loans it cannot collect. Lists, returns and
everything else stay fully usable. (Compare ANIMALS.01, which fails open: an
owner's address is auxiliary.)

**Registry adapter**, in `ee.taltech.ite4120.library.enrolmentregistry`: one
port, `EnrolmentRegistryAdapter.lookup(String isikukood)` → `EnrolmentStatus`
(`isikukood`, `institution`, `programme`, `enrolled`), with the same three
transports as `ownerregistry/`: the built-in imitated registry at
`/mock-enrolment-registry/persons/{isikukood}` (local profile), plain HTTPS+JSON,
and a forge-xroad skeleton against an EHIS-shaped service (`ehis/oppijaStaatus/v1`
— a teaching stand-in). Wire records keep the provider's Estonian names
(`isikukood`, `oppeasutus`, `oppekava`, `staatus`); the single mapper turns them
into the domain record. A person the registry does not know is *not enrolled*,
not an error. The registry's answer is never persisted. Chosen by
`library.enrolment-registry.mode` (`http` | `xroad`), mirroring
`animals.owner-registry.*`.

The imitated registry knows four people: `50011020017` Peeter Sepp (enrolled,
TalTech, IAIB), `60203150012` Liis Kask (enrolled, TalTech, IVSB),
`50305120021` Rasmus Tamm (enrolled, TalTech, IABB), `49912310011` Kadri Mägi
(graduated — not enrolled). Anyone else is unknown.

## UI  *(→ session 4)*

Three list pages on `ResourceList` and three forms under `frontend/src/pages/`,
routed at `/library/books`, `/library/students`, `/library/loans` (+ `/new` for
the forms); `/` keeps redirecting to `/animals`. Every input is an existing
`@helex/ui` or antd component; nothing from the calendar family.

| Screen | Element | Component | Rules |
| --- | --- | --- | --- |
| Book list | table | `ResourceList` | columns: ISBN (locked), title, author, year, copies, available (`AppTag`, green when > 0); search field; detail panel |
| | register button | `AppButtonPrimary` | navigates to the book form |
| | detail panel | `ResourceList.detailView` | book fields + its open loans (student, due date, overdue tag) |
| Book form | ISBN | `Input` | required; 10 or 13 digits; read-only on edit |
| | title, author | `Input` | required, ≤255 |
| | publication year | `InputNumber` | optional; ≤ current year |
| | copies | `InputNumber` | required; min 1 |
| | submit | `AppButtonPrimary` | 400/409 problem text shown verbatim in a notification |
| Student list | table | `ResourceList` | columns: student code (locked), name, e-mail, open loans, overdue (`AppTag` when > 0); search field; detail panel |
| | register button | `AppButtonPrimary` | navigates to the student form |
| | detail panel | `ResourceList.detailView` | student fields + live enrolment (institution, programme) fetched via `/enrolment`; shows "registry unavailable" on 502 — the list itself never depends on the registry |
| Student form | student code | `Input` | required, ≤20 |
| | first name, last name | `Input` | required, ≤100 |
| | personal code | `Input` | pattern `\d{11}` — "Try 50011020017 — the imitated registry lists this person as enrolled" |
| | e-mail | `Input` (type email) | optional, ≤255 |
| | submit | `AppButtonPrimary` | 409 "not enrolled" and 502 shown verbatim |
| Loan list | table | `ResourceList` | columns: book, student, loaned on, due on, status (`AppStatusTag`: open / overdue / returned); status filter (`Select`); search by student or book; "Return" row action on open loans (`AppPopconfirm` → `POST …/return`) |
| | lend button | `AppButtonPrimary` | navigates to the loan form |
| Loan form | student | `Select` (`showSearch`) | required; options from `/students` |
| | book | `Select` (`showSearch`) | required; options from `/books?available=true`, label shows free copies |
| | loaned on | `DatePicker` | default today; not in the future |
| | due on | `DatePicker` | default loaned on + 28 days; after loaned on |
| | submit | `AppButtonPrimary` | 409 reasons (no free copy, five loans, overdue, not enrolled) and 502 shown verbatim |

## Business tests  *(→ executable in `LibraryBusinessRulesIT`)*

Run like `AnimalBusinessRulesIT`: Spring Boot + Testcontainers PostgreSQL, the
whole changelog applied from empty. The enrolment registry is an in-memory fake
`EnrolmentRegistryAdapter` bean supplied by the test — the port is the seam, no
HTTP involved; one test flips it to "unreachable".

1. **A copy can be lent only while one is free.**
   Given a book with one copy that is out on loan, when a second student borrows
   it, then the loan is refused as a conflict ("no free copy"). And when the first
   loan is returned, the second student can borrow it.
2. **A student holds at most five open loans.**
   Given a student with five open loans, when a sixth is requested, then it is
   refused as a conflict; after one return the request succeeds.
3. **An overdue loan blocks new loans.**
   Given a student whose loan was due yesterday, when a new loan is requested,
   then it is refused as a conflict; after the overdue book is returned, the new
   loan succeeds.
4. **Enrolment is a precondition, and a silent registry is not a pass.**
   Given the registry lists a person as not enrolled, when they are registered as
   a student, then registration is refused as a conflict. And given the registry
   cannot answer, when a loan is requested for an enrolled student, then the
   request fails with the registry error and no loan is recorded.
5. **A loan is returned once.**
   Given a returned loan, when it is returned again, then it is refused as a
   conflict naming the original return date.
6. **Open loans pin their book and student.**
   Given a book with an open loan, when it is retired, then it is refused as a
   conflict; after the return it can be retired, and its ISBN may then be reused
   by a new book — retirement is a soft delete.
