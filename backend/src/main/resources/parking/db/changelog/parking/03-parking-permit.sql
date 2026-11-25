--liquibase formatted sql

--changeset ite4120:parking-03-parking-permit
--comment Accessible parking permit table

create table parking.parking_permit (
    id                  bigint default nextval('core.seq_id') not null,
    application_id      bigint not null,
    permit_number       varchar(50) not null,
    valid_from          date not null,
    valid_until         date not null,
    status              varchar(20) not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint parking_permit_pk primary key (id),
    constraint parking_permit_application_fk
        foreign key (application_id)
        references parking.permit_application (id),
    constraint parking_permit_number_uk unique (permit_number)
);

select core.create_table_metadata('parking.parking_permit');
