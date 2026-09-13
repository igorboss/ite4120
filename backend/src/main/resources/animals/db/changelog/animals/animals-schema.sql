--liquibase formatted sql
--changeset ite4120:animals-schema
--comment Create the animals schema. One schema per component — boundaries in the
--comment database mirror boundaries in the code.

create schema if not exists animals;
