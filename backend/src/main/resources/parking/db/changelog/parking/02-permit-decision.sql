--liquibase formatted sql

--changeset ite4120:parking-02-permit-decision
--comment Accessible parking permit application decision table

create table parking.permit_decision (
    id                  bigint default nextval('core.seq_id') not null,
    application_id      bigint not null,
    decision_date       date not null,
    decision_type       varchar(20) not null,
    decision_reason     varchar(500),
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint permit_decision_pk primary key (id),
    constraint permit_decision_application_fk
        foreign key (application_id)
        references parking.permit_application (id),
    constraint permit_decision_type_chk
        check (decision_type in ('APPROVED', 'REJECTED'))
);

select core.create_table_metadata('parking.permit_decision');
