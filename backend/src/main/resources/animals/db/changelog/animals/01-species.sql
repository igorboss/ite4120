--liquibase formatted sql
--changeset ite4120:animals-01-species
--comment Species reference table — the lookup list the animal table points at.
--comment Natural-key reference data: no core.seq_id, no sys columns. Compare with
--comment 02-animal.sql, which is a full business table.

create table animals.species (
    code    text not null,
    name    text not null,
    constraint species_pk primary key (code)
);

insert into animals.species (code, name) values
    ('DOG',    'Dog'),
    ('CAT',    'Cat'),
    ('HORSE',  'Horse'),
    ('PARROT', 'Parrot');
