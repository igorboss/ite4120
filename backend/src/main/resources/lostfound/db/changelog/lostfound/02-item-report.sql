--liquibase formatted sql

--changeset ite4120:lostfound-02-item-report
--comment Create the main Lost and Found report table
create table lostfound.item_report (
    id                  bigint default nextval('core.seq_id') not null,
    report_type         text not null,
    item_name           text not null,
    description         text not null,
    report_date         date not null,
    category_code       text not null,
    location_code       text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint item_report_pk primary key (id),
    constraint item_report_type_chk check (report_type in ('LOST', 'FOUND')),
    constraint item_report_category_fk foreign key (category_code) references lostfound.category (code),
    constraint item_report_location_fk foreign key (location_code) references lostfound.location (code)
);

select core.create_table_metadata('lostfound.item_report');

create index item_report_type_idx on lostfound.item_report (report_type) where (sys_status = 'A');
create index item_report_category_idx on lostfound.item_report (category_code) where (sys_status = 'A');
create index item_report_location_idx on lostfound.item_report (location_code) where (sys_status = 'A');
--
