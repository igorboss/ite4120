--liquibase formatted sql

--changeset ite4120:lostfound-01-reference-tables
--comment Create category and location reference tables
create table lostfound.category (
    code                text not null,
    name                text not null,
    constraint category_pk primary key (code),
    constraint category_name_ukey unique (name)
);

create table lostfound.location (
    code                text not null,
    name                text not null,
    address             text not null,
    constraint location_pk primary key (code)
);
--

--changeset ite4120:lostfound-01-reference-seed
--comment Seed the initial Parma categories and locations
insert into lostfound.category (code, name) values
    ('DOCUMENTS', 'Documents'),
    ('ELECTRONICS', 'Electronics'),
    ('CLOTHING', 'Clothing'),
    ('KEYS', 'Keys'),
    ('BAGS', 'Bags'),
    ('OTHER', 'Other')
on conflict do nothing;

insert into lostfound.location (code, name, address) values
    ('PARCO_DUCALE', 'Parco Ducale', 'Parco Ducale, Parma'),
    ('PIAZZA_GARIBALDI', 'Piazza Garibaldi', 'Piazza Garibaldi, Parma'),
    ('TRAIN_STATION', 'Parma Railway Station', 'Piazzale Carlo Alberto Dalla Chiesa, Parma'),
    ('PIAZZA_PACE', 'Piazza della Pace', 'Piazza della Pace, Parma')
on conflict do nothing;
--
