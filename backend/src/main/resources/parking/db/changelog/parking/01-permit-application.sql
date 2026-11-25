--liquibase formatted sql

--changeset ite4120:parking-01-permit-application
--comment Accessible parking permit application table

create table parking.permit_application (
    id                  bigint default nextval('core.seq_id') not null,
    personal_code       text not null,
    application_date    date not null,
    reason              varchar(500),
    sys_status           char(1),
    sys_version          int,
    sys_created_at       timestamptz,
    sys_created_by       text,
    sys_modified_at      timestamptz,
    sys_modified_by      text,
    constraint permit_application_pk primary key (id),
    constraint permit_application_personal_code_chk
        check (personal_code ~ '^[0-9]{11}$')
);

select core.create_table_metadata('parking.permit_application');
