---
id: LIBRARY-US-001
title: Lend books to enrolled students
state: Proposed
traces-to: [LIBRARY.01]
---

# LIBRARY-US-001 · Lend books to enrolled students

**As** a university library clerk
**I want** a register of books, students and loans
**so that** I can answer, for any book, whether a copy is on the shelf, and for
any student, what they hold and when it is due — without a card index, and
without phoning the dean's office to ask whether someone still studies here.

## Context

A university library lends to people who are currently enrolled. Enrolment is
not the library's fact to keep: it lives in the education information system
(EHIS in Estonia — over X-Road in production, an imitated registry in this
course). The library stores the student's code and personal code and asks the
registry whether that person is enrolled whenever it matters — at registration
and at every loan. One source of truth per fact, the same lesson as the animals
register's owner lookup, with the opposite failure policy: an owner's address is
decoration, enrolment is a precondition.

Books are the library's own: catalogue rows with a number of physical copies. A
loan is one copy in one student's hands until it is returned.

Out of scope: reservations and waiting lists, fines, multiple branches or reading
rooms, reminder e-mails, book condition and inventory audits.

## Acceptance criteria

- A book can be registered with an ISBN, title, author, optional publication
  year and a number of copies (at least one). An ISBN identifies exactly one
  **active** book; the ISBN of a retired book may be reused.
- A student can be registered with a student code, name, personal code and
  optional e-mail. A student code identifies exactly one **active** student.
  Registration is refused when the education registry does not list the person
  as currently enrolled — and refused, not waved through, when the registry
  cannot be reached.
- A copy is lent to a student for a period (four weeks by default). The loan is
  refused when no copy is free, when the student already holds five open loans,
  or when the student has an overdue loan. Enrolment is checked again at every
  loan.
- Returning closes the loan and frees the copy; a closed loan cannot be returned
  twice.
- Obvious nonsense is refused with a clear message: malformed ISBN or personal
  code, publication year in the future, due date on or before the loan date,
  return date before the loan date, an unknown book or student.
- Book, student and loan lists are searchable; loans can be narrowed to open,
  overdue or returned, and to one student or one book.
- A book or student with an open loan cannot be retired.
