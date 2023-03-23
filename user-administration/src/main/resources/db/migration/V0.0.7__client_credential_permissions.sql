create table client_credential_permission
(
    id                   bigint primary key,
    role                 varchar(20)  not null,
    application          varchar(20)  not null,
    client_credential_id varchar(50)  not null,
    alias                varchar(100) not null,
    comment              varchar(100) not null,
    constraint client_credential_application_unique unique (application, client_credential_id),
    constraint alias_application_unique unique (application, alias)
);

alter table permission_restriction
    add column client_credential_permission_id bigint;


CREATE SEQUENCE client_credential_permission_seq START WITH 1000 INCREMENT BY 1;