# Group Project for IT and eGov Technologies

**University:** Tallinn University of Technology (TalTech)  
**Course:** ITE4120 Introduction to IT and eGov Technologies  
**Semester:** Autumn 2026

## About
A group project for the IT and eGov Technologies course: a library book catalogue and lending component (BOOKS), built on the course template.

## Team
- Mari Tarum-Seilenthal
- Heiki Ilisson
- Helena Toompalu

## Project requirements
- Database with two tables: `books.book` and `books.copy` (foreign key copy → book)
- APIs: list/search books, register a book, update lending, remove a book
- Borrower name fetched from the mock registry (X-Road style)
- Two forms: register form and lending form, with field validation as in the spec
- At least two business tests as passing automated tests
- Each team member commits their own part and keeps a work diary

## Work plan

Tables: `books.book` and `books.copy` (genres are a fixed list inside `book`).

| # | Step | Who | Check after |
|---|------|-----|-------------|
| 1 | Set up the app on each laptop (see setup steps) | All | Animals page opens at http://localhost:18640 |
| 2 | Fix the docs: rename user story to `BOOKS-US-001.md` and add header line; spec table names `book` + `copy` | Mari (story), Helena (spec) | Each reads the other's file; names match |
| 3 | `books-schema.sql`, `01-book.sql`, both `changelog.xml` files, 1 line in master changelog | Mari | `.\scripts\check-changesets.ps1` passes; backend starts; Animals still works |
| 4 | `02-copy.sql` (FK → book) | Heiki (after step 3 is merged) | Same checks as step 3 |
| 5 | API: GET, POST, PUT, DELETE + borrower lookup (copy teacher's `ownerregistry`) | Helena | Each endpoint works in Swagger UI (http://localhost:18440/swagger-ui.html) |
| 6 | Register form | Mari | Wrong input (letters in pages, empty title) shows an error; correct input appears in the list |
| 6 | Lending form | Heiki | Wrong input (10-digit personal code) is refused; correct input is saved |
| 7 | 2–3 automated tests from the spec | Helena | `cd backend` then `.\gradlew test` passes |
| 8 | Full run-through on the presentation laptop | All | Done before 27.11 |

### Rules for everyone

- Never commit straight to `main`: make a branch, open a pull request, someone else merges it.
- Each person commits their own part, so it shows under their name.
- Never edit a merged SQL file; add a new numbered file instead.
- Update your work diary after each work session.

### Deadlines

- Defence: 27.11.2026
- All deliverables: 11.12.2026