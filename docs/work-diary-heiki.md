# Work diary — Heiki

## 2026-10-21

Time spent ca 2 hours

**Work completed:**

- Reviewed course materials and lecture notes.
- Set up Git and GitHub Desktop.
- Looked through the group repository and tried to understand the basic GitHub workflow.

**AI assistance:**

- Used AI to better understand course requirements and GitHub basics.

## 2026-10-23

Time spent ca 2 hours

**Work completed:**

- Reviewed the lecture materials about front-end development, REST APIs and Storybook.
- Tried to understand how the front end, backend and database work together.

## 2026-10-25

Time spent ca 1 hour

**Work completed:**

- Participated in a team call about the library application.
- Discussed the book register, lending register, statuses and main rules.
- Discussed possible validation rules and tests.

## 2026-11-03

Time spent ca 2 hours

**Work completed:**

- Reviewed the team user story, specification and GitHub repository.
- Updated the user story acceptance criteria to separate book registration data from lending information.
- Created a separate branch, committed the change and opened a pull request for review.
- Reviewed Helena's specification pull request and noticed that it uses a book + physical copy data model, which differs from the earlier user story.
- Decided not to merge or make further changes before discussing the data model with the team.

**AI assistance:**

- Used AI to review the user story and specification.
- Used AI to better understand GitHub branches, commits and pull requests.
- Used AI to identify the differences between the user story and the proposed specification.

**Corrections and decisions:**

- Kept my pull request open for discussion.
- Decided to confirm the final data model during the next team call before making further changes.

## 2026-11-08

Time spent ca 2 hours

**Work completed:**

- Participated in a team call about the library application.
- Discussed the book register, lending register, statuses and main rules.
- Discussed possible validation rules and tests.

## 2026-11-10

Time spent ca 4 hours

**Work completed:**

- Set up and tested the project development environment.
- Created my Git branch heiki-copy-table.
- Created the Liquibase migration for the books.copy table, including the foreign key, status and lending validation rules.
- Added the migration to the changelog and successfully tested it with the backend and PostgreSQL.
- Installed and used DBeaver to verify the table structure, constraints and indexes.
- Committed and pushed the changes to the group GitHub repository.

**AI assistance:**

- Used AI for step-by-step guidance with Liquibase, Git, development environment setup and troubleshooting.
- Used AI to review the database structure against the project specification and lecturer examples.
  
**Corrections and decisions:**
- Confirmed that ISBN identifies a book title/edition and physical copies are stored separately.
- Kept lending information at copy level.

## 2026-11-15

Time spent ca 1,5 hours

**Work completed:**

- Participated in a team call about the current status and next steps.

## 2026-11-22

Time spent ca 4 hours

**Work completed:**

- Participated in a team call about the library application.
- Discussed the book register, lending register, statuses and main rules.
- Discussed possible validation rules and tests.
