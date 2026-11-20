--liquibase formatted sql

--changeset ite4120:books-03-book-fixes
--comment Add sys columns and active ISBN uniqueness

alter table books.book
    add column sys_status          char(1),
    add column sys_version         int,
    add column sys_created_at      timestamptz,
    add column sys_created_by      text,
    add column sys_modified_at     timestamptz,
    add column sys_modified_by     text;

select core.create_table_metadata('books.book');

create unique index book_isbn_active_ukey
    on books.book (isbn)
    where (sys_status = 'A');

--
