--liquibase formatted sql
--changeset ite4120:animals-02-animal
--comment The main business table. Note the platform idioms:
--comment   * id from the SHARED sequence core.seq_id (ticket dispenser for the whole DB)
--comment   * sys_* columns declared here, FILLED by the core.sys_columns() trigger
--comment   * the trigger is installed by core.create_table_metadata(), not by hand
--comment   * soft delete: rows are never removed, sys_status goes A -> C
--comment   * owner_isikukood is TEXT — an identifier, never a number (S3!)

create table animals.animal (
    id                 bigint default nextval('core.seq_id') not null,
    registry_code      text not null,
    name               text not null,
    species_code       text not null,
    birth_date         date,
    owner_isikukood    text,
    -- sys columns — managed by core.sys_columns() trigger, no app-side defaults
    sys_status         char(1),
    sys_version        int,
    sys_created_at     timestamptz,
    sys_created_by     text,
    sys_modified_at    timestamptz,
    sys_modified_by    text,
    constraint animal_pk           primary key (id),
    constraint animal_species_fk   foreign key (species_code) references animals.species (code),
    constraint animal_isikukood_chk check (owner_isikukood is null or owner_isikukood ~ '^[0-9]{11}$')
);

select core.create_table_metadata('animals.animal');

-- Unique among ACTIVE rows only: a retired animal's code may be reused.
create unique index animal_registry_code_ukey
    on animals.animal (registry_code) where (sys_status = 'A');

create index animal_species_idx on animals.animal (species_code);
