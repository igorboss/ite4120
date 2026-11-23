--liquibase formatted sql

--changeset ite4120:lostfound-schema
--comment Create the Lost and Found component schema
create schema if not exists lostfound;
--
