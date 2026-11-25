--liquibase formatted sql

--changeset ite4120:parking-schema
--comment Create the parking schema
/* One schema per component — boundaries in the database mirror boundaries in
   the code. */
create schema if not exists parking;
