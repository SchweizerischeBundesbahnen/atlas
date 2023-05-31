-----------------------------------------------------------------------------------------
-- User / Client
-----------------------------------------------------------------------------------------

CREATE TABLE permission
(
    id          bigint primary key,
    identifier  VARCHAR(50),
    role        VARCHAR(20) not null,
    application VARCHAR(20) not null,
    constraint user_application_unique unique (application, identifier)
);

CREATE SEQUENCE permission_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Permission Restriction
-----------------------------------------------------------------------------------------

CREATE TABLE permission_restriction
(
    id                 bigint primary key,
    restriction        VARCHAR(32) not null,
    type               VARCHAR(50) not null,
    user_permission_id bigint
        constraint fk_user_permission_id references user_permission
);

CREATE SEQUENCE permission_restriction_seq START WITH 1000 INCREMENT BY 1;