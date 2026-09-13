# Liquibase guide — the worked example, changeset by changeset

The animals schema is built entirely by Liquibase from
`backend/src/main/resources/animals/db/changelog/`. Nothing is ever typed into
`psql` by hand: the changelog **is** the schema's biography, and every
environment — your laptop, a teammate's, CI, the defence demo — converges by
replaying the same steps in the same order.

## Anatomy of a changeset

```sql
--liquibase formatted sql
--changeset ite4120:animals-02-animal
--comment The main business table ...

create table animals.animal ( ... );
```

`author:id` (`ite4120:animals-02-animal`) is the changeset's identity. Liquibase
records it in `public.databasechangelog` together with a **checksum** of the SQL.
On every start it compares: not yet recorded → run it; recorded and unchanged →
skip it; recorded but **edited** → refuse to start.

## The one rule: released changesets are immutable

That checksum is why **you never edit a changeset that has run anywhere** — not
on a teammate's machine, not in CI. You append a new one. The worked example
demonstrates this exactly:

| File | What it teaches |
| --- | --- |
| `animals-schema.sql` | `create schema` — one schema per component |
| `01-species.sql` | Reference table: natural key, no sys columns |
| `02-animal.sql` | Business table: `core.seq_id` ids, `sys_*` columns, `create_table_metadata()` trigger install, partial unique index for soft-deleted rows |
| `03-vaccination.sql` | Child table with FK — with 01 and 02, three tables and two FKs: the gate-A minimum |
| `04-animal-add-chip-number.sql` | **The evolution lesson.** The chip number arrived after 02 shipped. We did not edit 02 — we appended an ALTER as its own changeset |
| `90-demo-data.sql` | Seed rows behind `context:demo` — drop the context in `application.yml` to start empty |

## The core includes

The master changelog's first include is not ours:

```xml
<include file="core-db/changelog/changelog.xml"/>
```

That path resolves **inside the published `org.helex.emr:commons-db-core` jar**.
It creates the shared `core` schema: the id sequence (`core.seq_id`), the
sys-column trigger machinery and the session functions our DDL calls. This is the
platform pattern: shared schema ships as a library, modules include it.

## Reset vs rollback

In development you do not roll back — you **reset**: `scripts/reset-db` destroys
the database and the next backend start replays the whole changelog from empty.
The Testcontainers tests do the same on every run, which is why a changelog that
only works "from where my database happens to be" fails CI immediately.

## For your own component

Copy the shape exactly: `<yours>/db/changelog/<yours>/` with a schema changeset,
numbered table changesets, and one include added to the master changelog. If you
need to change a table you already merged — that is an appended changeset. Now
you know why.
