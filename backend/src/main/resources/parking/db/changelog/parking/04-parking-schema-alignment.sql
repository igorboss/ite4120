--liquibase formatted sql

--changeset ali-al-saedi:parking-04-schema-alignment
--comment Align parking database schema with the confirmed specification

alter table parking.permit_application
    add column status varchar(20) not null default 'RECEIVED';

alter table parking.permit_application
    add constraint permit_application_status_chk
        check (status in ('RECEIVED', 'DECIDED'));

alter table parking.permit_decision
    add constraint permit_decision_application_uk
        unique (application_id);

alter table parking.parking_permit
    add constraint parking_permit_application_uk
        unique (application_id);
