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
    id            bigint primary key,
    restriction   VARCHAR(32) not null,
    type          VARCHAR(50) not null,
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

-------------------------------------------------------
-------------------  PRM Data  ------------------------

------------------  STOP PLACE  -----------------------
CREATE TABLE stop_place_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    free_text                       VARCHAR(2000),
    address                         VARCHAR(2000),
    zip_code                        VARCHAR(50),
    city                            VARCHAR(50),
    alternative_transport           VARCHAR(50)  NOT NULL,
    alternative_transport_condition VARCHAR(2000),
    assistance_availability         VARCHAR(50)  NOT NULL,
    alternative_condition           VARCHAR(2000),
    assistance_service              VARCHAR(50)  NOT NULL,
    audio_ticket_machine            VARCHAR(50)  NOT NULL,
    additional_info                 VARCHAR(2000),
    dynamic_audio_system            VARCHAR(50)  NOT NULL,
    dynamic_optic_system            VARCHAR(50)  NOT NULL,
    info_ticket_machine             VARCHAR(2000),
    interoperable                   BOOLEAN,
    URL                             VARCHAR(500),
    visual_info                     VARCHAR(50)  NOT NULL,
    wheelchair_ticket_machine       VARCHAR(50)  NOT NULL,
    assistance_request_fulfilled    VARCHAR(50)  NOT NULL,
    ticket_machine                  VARCHAR(50)  NOT NULL,
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE stop_place_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE stop_place_version
    ADD CONSTRAINT stop_place_sloid_unique UNIQUE (sloid, valid_from);

-- Stop Place  MeansOfTransport Mapping
CREATE TABLE stop_place_version_means_of_transport
(
    stop_place_version_id BIGINT      NOT NULL,
    means_of_transport    VARCHAR(50) NOT NULL
);

-----------------  REFERENCE POINT  ---------------------

CREATE TABLE reference_point_version
(
    id                         BIGINT       NOT NULL PRIMARY KEY,
    sloid                      VARCHAR(500) NOT NULL,
    parent_service_point_sloid VARCHAR(500) NOT NULL,
    number                     INTEGER      NOT NULL,
    designation                VARCHAR(50)  NOT NULL,
    main_reference_point       BOOLEAN      NOT NULL,
    reference_point_type       VARCHAR(50)  NOT NULL,
    valid_from                 DATE         NOT NULL,
    valid_to                   DATE         NOT NULL,
    creation_date              TIMESTAMP    NOT NULL,
    creator                    VARCHAR(50),
    edition_date               TIMESTAMP    NOT NULL,
    editor                     VARCHAR(50),
    version                    BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE reference_point_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE reference_point_version
    ADD CONSTRAINT reference_point_sloid_unique UNIQUE (sloid, valid_from);


------------------ PLATFORM ---------------------------
CREATE TABLE platform_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    boarding_device                 VARCHAR(50),
    additional_info                 VARCHAR(2000),
    advice_access_info              VARCHAR(2000),
    contrasting_areas               VARCHAR(50),
    dynamic_audio                   VARCHAR(50),
    dynamic_visual                  VARCHAR(50),
    height                          NUMERIC(10, 3)   NULL,
    inclination                     NUMERIC(10, 3)   NULL,
    inclination_longitudinal        NUMERIC(10, 3)   NULL,
    inclination_width               NUMERIC(10, 3)   NULL,
    level_access_wheelchair         VARCHAR(50),
    partial_elevation               VARCHAR(50),
    superelevation                  NUMERIC(10, 3)   NULL,
    tactile_system                  VARCHAR(50),
    vehicle_access                  VARCHAR(50),
    wheelchair_area_length          NUMERIC(10, 3)   NULL,
    wheelchair_area_width           NUMERIC(10, 3)   NULL,
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE platform_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE platform_version
    ADD CONSTRAINT platform_sloid_unique UNIQUE (sloid, valid_from);

CREATE TABLE platform_version_info_opportunities
(
    platform_version_id BIGINT      NOT NULL,
    info_opportunities    VARCHAR(50) NOT NULL
);

------------------ TOILET ---------------------------
CREATE TABLE toilet_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    designation                     VARCHAR(50),
    info                            VARCHAR(2000),
    wheelchair_toilet               VARCHAR(50),
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE toilet_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE toilet_version
    ADD CONSTRAINT toilet_sloid_unique UNIQUE (sloid, valid_from);


------------------ TICKET COUNTER ---------------------------
CREATE TABLE ticket_counter_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    designation                     VARCHAR(50),
    info                            VARCHAR(2000),
    induction_loop                  VARCHAR(50),
    opening_hours                   VARCHAR(2000),
    wheelchair_access               VARCHAR(50),
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE ticket_counter_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE ticket_counter_version
    ADD CONSTRAINT ticket_counter_sloid_unique UNIQUE (sloid, valid_from);


------------------ INFORMATION DESK ---------------------------
CREATE TABLE information_desk_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    designation                     VARCHAR(50),
    info                            VARCHAR(2000),
    induction_loop                  VARCHAR(50),
    opening_hours                   VARCHAR(2000),
    wheelchair_access               VARCHAR(50),
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE information_desk_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE information_desk_version
    ADD CONSTRAINT information_desk_sloid_unique UNIQUE (sloid, valid_from);


------------------ PARKING LOT ---------------------------
CREATE TABLE parking_lot_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    designation                     VARCHAR(50),
    info                            VARCHAR(2000),
    places_available                VARCHAR(50),
    prm_places_available            VARCHAR(2000),
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50),
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE parking_lot_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE parking_lot_version
    ADD CONSTRAINT parking_lot_sloid_unique UNIQUE (sloid, valid_from);


------------------ RELATION     ---------------------------
CREATE TABLE relation_version
(
    id                           BIGINT       NOT NULL PRIMARY KEY,
    sloid                        VARCHAR(500) NOT NULL,
    number                       INTEGER      NOT NULL,
    parent_service_point_sloid   VARCHAR(500) NOT NULL,
    tactile_visual_marks         VARCHAR(50),
    contrasting_areas            VARCHAR(50),
    step_free_access               VARCHAR(50),
    reference_point_element_type VARCHAR(50),
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50),
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50),
    version                      BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE relation_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE relation_version
    ADD CONSTRAINT relation_sloid_unique UNIQUE (sloid, valid_from);
