-- Data Shared via Kafka to this service

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
    permission_id bigint
        constraint fk_user_permission_id references permission
);

CREATE SEQUENCE permission_restriction_seq START WITH 1000 INCREMENT BY 1;

-- Data Shared via Kafka to this service

-- Business Organisation Version
create table shared_business_organisation_version
(
    id                  bigint primary key,
    sboid               varchar(32) not null,
    abbreviation_de     varchar(10) not null,
    abbreviation_fr     varchar(10) not null,
    abbreviation_it     varchar(10) not null,
    abbreviation_en     varchar(10) not null,
    description_de      varchar(60) not null,
    description_fr      varchar(60) not null,
    description_it      varchar(60) not null,
    description_en      varchar(60) not null,
    organisation_number integer     not null,
    status              varchar(50) not null,
    valid_from          date        not null,
    valid_to            date        not null
);

-- PRM Data

CREATE TABLE stop_place_version
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    slnid                 VARCHAR(500) NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    valid_from            DATE         NOT NULL,
    valid_to              DATE         NOT NULL,
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE stop_place_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE stop_place_version
    ADD CONSTRAINT stop_place_slnid_unique UNIQUE (slnid, valid_from);
