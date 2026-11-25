--liquibase formatted sql

--changeset ite4120:parking-05-permit-validation
--comment Add parking permit validity and status constraints
alter table parking.parking_permit
    add constraint parking_permit_validity_chk
        check (valid_until >= valid_from);

alter table parking.parking_permit
    add constraint parking_permit_status_chk
        check (status in ('ACTIVE'));
--
