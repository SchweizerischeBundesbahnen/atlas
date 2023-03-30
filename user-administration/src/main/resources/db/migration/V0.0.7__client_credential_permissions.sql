create table client_credential_permission
(
    id                   bigint primary key,
    role                 varchar(20) not null,
    application          varchar(20) not null,
    client_credential_id varchar(50) not null,
    alias                varchar(100),
    comment              varchar(100),
    creation_date        TIMESTAMP   NOT NULL,
    creator              VARCHAR(50) NOT NULL,
    edition_date         TIMESTAMP   NOT NULL,
    editor               VARCHAR(50) NOT NULL,
    version              BIGINT      NOT NULL DEFAULT 0,
    constraint client_credential_application_unique unique (application, client_credential_id),
    constraint alias_application_unique unique (application, alias)
);

alter table permission_restriction
    add column client_credential_permission_id bigint;
alter table permission_restriction
    alter column user_permission_id drop not null;
alter table permission_restriction
    add constraint fk_client_credential_permission_id foreign key (client_credential_permission_id) references client_credential_permission (id);


CREATE SEQUENCE client_credential_permission_seq START WITH 1000 INCREMENT BY 1;