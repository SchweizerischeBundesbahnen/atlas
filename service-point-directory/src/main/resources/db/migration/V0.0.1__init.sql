-----------------------------------------------------------------------------------------
-- Service Point Version - DIENSTSTELLEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version
(
    id                   BIGINT      NOT NULL PRIMARY KEY,
    number               INTEGER     NOT NULL,
    check_digit          SMALLINT    NOT NULL,
    number_short         INTEGER     NOT NULL,
    uic_country_code     SMALLINT    NOT NULL,
    designation_long     VARCHAR(50) NULL,
    designation_official VARCHAR(30) NOT NULL,
    abbreviation         VARCHAR(6)  NULL,
    status_didok3        SMALLINT    NULL,
    said                 VARCHAR(38) NOT NULL,
    has_geolocation      BOOLEAN     NULL     DEFAULT FALSE,
    valid_from           DATE        NOT NULL,
    valid_to             DATE        NOT NULL,
    creation_date        TIMESTAMP   NOT NULL,
    creator              VARCHAR(50) NOT NULL,
    edition_date         TIMESTAMP   NOT NULL,
    editor               VARCHAR(50) NOT NULL,
    version              BIGINT      NOT NULL DEFAULT 0
);

--ALTER TABLE service_point_version ADD CONSTRAINT service_point_version_unique UNIQUE (version, valid_from);

CREATE SEQUENCE service_point_version_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE service_point_version
    ADD CONSTRAINT spv_number_unique UNIQUE (number, valid_from);

-- search api field indexes
CREATE INDEX spv_number_idx ON service_point_version (number);
CREATE INDEX spv_numbershort_idx ON service_point_version (number_short);
CREATE INDEX spv_countrycode_idx ON service_point_version (uic_country_code);
CREATE INDEX spv_designation_idx ON service_point_version (designation_long);
CREATE INDEX spv_designationlong_idx ON service_point_version (designation_official);
CREATE INDEX spv_abbrevation_idx ON service_point_version (abbreviation);
CREATE INDEX spv_status_didok3_idx ON service_point_version (status_didok3);
CREATE INDEX spv_said_idx ON service_point_version (said);
CREATE INDEX spv_geolocation_idx ON service_point_version (has_geolocation);
CREATE INDEX spv_validity_idx ON service_point_version (valid_from, valid_to);
CREATE INDEX spv_creation_date_idx ON service_point_version (creation_date);
CREATE INDEX spv_edition_date_idx ON service_point_version (edition_date);

-----------------------------------------------------------------------------------------
-- Service Point Version Geolocation - DIENSTSTELLEN_FC
-----------------------------------------------------------------------------------------


CREATE TABLE service_point_version_geolocation
(
    id                       BIGINT       NOT NULL PRIMARY KEY,
    service_point_version_id BIGINT       NOT NULL,
    source_spatial_ref       INTEGER      NOT NULL,
    e_lv03                   NUMERIC      NULL,
    n_lv03                   NUMERIC      NULL,
    e_lv95                   NUMERIC      NULL,
    n_lv95                   NUMERIC      NULL,
    e_wgs84                  NUMERIC      NULL,
    n_wgs84                  NUMERIC      NULL,
    height                   NUMERIC      NULL,
    iso_country_code         VARCHAR(2)   NULL,
    swiss_canton_fso_number  SMALLINT     NULL,
    swiss_canton_name        VARCHAR(50)  NULL,
    swiss_canton_number      SMALLINT     NULL,
    swiss_district_name      VARCHAR(255) NULL,
    swiss_district_number    SMALLINT     NULL,
    swiss_municipality_name  VARCHAR(255) NULL,
    swiss_locality_name      VARCHAR(255) NULL,
    creation_date            TIMESTAMP    NOT NULL,
    creator                  VARCHAR(50)  NOT NULL,
    edition_date             TIMESTAMP    NOT NULL,
    editor                   VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id)
);

CREATE SEQUENCE service_point_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-- search api field indexes
CREATE INDEX spvgeo_spatialref_idx ON service_point_version_geolocation (source_spatial_ref);
CREATE INDEX spvgeo_coordlv03_idx ON service_point_version_geolocation (e_lv03, n_lv03);
CREATE INDEX spvgeo_coordlv95_idx ON service_point_version_geolocation (e_lv95, n_lv95);
CREATE INDEX spvgeo_coordwgs84_idx ON service_point_version_geolocation (e_wgs84, n_wgs84);
CREATE INDEX spvgeo_countrycode_idx ON service_point_version_geolocation (iso_country_code);

-----------------------------------------------------------------------------------------
-- Service Point Comment - DS_BEMERKUNGEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_comment
(
    id                   BIGINT        NOT NULL PRIMARY KEY,
    service_point_number INTEGER       NOT NULL,
    comment              VARCHAR(2000) NOT NULL,
    creation_date        TIMESTAMP     NOT NULL,
    creator              VARCHAR(50)   NOT NULL,
    edition_date         TIMESTAMP     NOT NULL,
    editor               VARCHAR(50)   NOT NULL,
    CONSTRAINT fk_service_point_number
        FOREIGN KEY (service_point_number)
            REFERENCES service_point_version (number)
);

CREATE SEQUENCE service_point_comment_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Category Lookup Table - KATEGORIEN
-----------------------------------------------------------------------------------------

CREATE TABLE category
(
    id             SMALLINT     NOT NULL PRIMARY KEY,
    is_active      BOOLEAN      NULL DEFAULT TRUE,
    is_visible     BOOLEAN      NULL DEFAULT TRUE,
    designation_de VARCHAR(30)  NOT NULL,
    designation_fr VARCHAR(30)  NULL,
    designation_it VARCHAR(30)  NULL,
    designation_en VARCHAR(30)  NULL,
    description    VARCHAR(255) NOT NULL,
    creation_date  TIMESTAMP    NOT NULL,
    creator        VARCHAR(50)  NOT NULL,
    edition_date   TIMESTAMP    NOT NULL,
    editor         VARCHAR(50)  NOT NULL
);

CREATE SEQUENCE category_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE category
    ADD CONSTRAINT category_designation_de_unique UNIQUE (designation_de);

CREATE INDEX category_is_active_idx ON category (is_active);
CREATE INDEX category_is_visible_idx ON category (is_visible);

-----------------------------------------------------------------------------------------
-- Service Point Category DS_KATEGORIEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_category
(
    id                    BIGINT      NOT NULL PRIMARY KEY,
    service_point_version BIGINT      NOT NULL,
    category_id           SMALLINT    NOT NULL,
    creation_date         TIMESTAMP   NOT NULL,
    creator               VARCHAR(50) NOT NULL,
    edition_date          TIMESTAMP   NOT NULL,
    editor                VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version)
            REFERENCES service_point_version (id),
    CONSTRAINT fk_category_id
        FOREIGN KEY (category_id)
            REFERENCES category (id)
);

CREATE SEQUENCE service_point_category_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Operation Point Type BETRIEBSPUNKT_ARTEN
-----------------------------------------------------------------------------------------

CREATE TABLE operating_point_type
(
    id               SMALLINT     NULL PRIMARY KEY,
    allowed_features VARCHAR(512) NOT NULL,
    designation_de   VARCHAR(50)  NOT NULL,
    designation_fr   VARCHAR(50)  NOT NULL,
    designation_it   VARCHAR(50)  NOT NULL,
    designation_en   VARCHAR(50)  NOT NULL,
    abbreviation_de  VARCHAR(10)  NULL,
    abbreviation_fr  VARCHAR(10)  NULL,
    abbreviation_it  VARCHAR(10)  NULL,
    abbreviation_en  VARCHAR(10)  NULL,
    creation_date    TIMESTAMP    NOT NULL,
    creator          VARCHAR(50)  NOT NULL,
    edition_date     TIMESTAMP    NOT NULL,
    editor           VARCHAR(50)  NOT NULL
);

CREATE SEQUENCE operating_point_type_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE operating_point_type
    ADD CONSTRAINT operating_point_type_designation_de_unique UNIQUE (designation_de);

-----------------------------------------------------------------------------------------
-- Operation Point BETRIEBSPUNKTE
-----------------------------------------------------------------------------------------

CREATE TABLE operating_point
(
    id                       BIGINT      NULL PRIMARY KEY,
    service_point_version_id BIGINT      NOT NULL,
    operating_point_type_id  SMALLINT    NULL,
    creation_date            TIMESTAMP   NOT NULL,
    creator                  VARCHAR(50) NOT NULL,
    edition_date             TIMESTAMP   NOT NULL,
    editor                   VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id),
    CONSTRAINT fk_operating_point_type_id
        FOREIGN KEY (operating_point_type_id)
            REFERENCES operating_point_type (id)
);

CREATE SEQUENCE operating_point_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Operation Point without Timetable BETRIEBSPUNKTEOHNEFAHRPLAN
-----------------------------------------------------------------------------------------

CREATE TABLE operating_point_without_timetable
(
    id                       BIGINT      NULL PRIMARY KEY,
    service_point_version_id BIGINT      NOT NULL,
    operating_point_type_id  SMALLINT    NULL,
    creation_date            TIMESTAMP   NOT NULL,
    creator                  VARCHAR(50) NOT NULL,
    edition_date             TIMESTAMP   NOT NULL,
    editor                   VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id),
    CONSTRAINT fk_operating_point_type_id
        FOREIGN KEY (operating_point_type_id)
            REFERENCES operating_point_type (id)
);

CREATE SEQUENCE operating_point_without_timetable_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Operation Point with Timetable FAHRPLANBETRIEBSPUNKT
-----------------------------------------------------------------------------------------

CREATE TABLE operating_point_with_timetable
(
    id                               BIGINT      NULL PRIMARY KEY,
    service_point_version_id         BIGINT      NOT NULL,
    operating_point_type_id          SMALLINT    NULL,
    is_operating_point_kilometer     BOOLEAN     NULL DEFAULT FALSE,
    is_operating_point_route_network BOOLEAN     NULL DEFAULT FALSE,
    operating_point_kilometer_master INTEGER     NULL,
    creation_date                    TIMESTAMP   NOT NULL,
    creator                          VARCHAR(50) NOT NULL,
    edition_date                     TIMESTAMP   NOT NULL,
    editor                           VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id),
    CONSTRAINT fk_operating_point_type_id
        FOREIGN KEY (operating_point_type_id)
            REFERENCES operating_point_type (id),
    CONSTRAINT fk_service_point_number
        FOREIGN KEY (operating_point_kilometer_master)
            REFERENCES service_point_version (number)
);

CREATE SEQUENCE operating_point_with_timetable_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Freight Service Point BEDIENPUNKTE
-----------------------------------------------------------------------------------------

CREATE TABLE freight_service_point
(
    id                               BIGINT      NULL PRIMARY KEY,
    service_point_version_id         BIGINT      NOT NULL,
    sort_code_of_destination_station VARCHAR(5)  NULL,
    creation_date                    TIMESTAMP   NOT NULL,
    creator                          VARCHAR(50) NOT NULL,
    edition_date                     TIMESTAMP   NOT NULL,
    editor                           VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id)
);

CREATE SEQUENCE freight_service_point_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Stop Place Type HALTESTELLENTYPEN
-----------------------------------------------------------------------------------------

CREATE TABLE stop_place_type
(
    id                 SMALLINT    NULL PRIMARY KEY,
    is_active          BOOLEAN     NULL DEFAULT TRUE,
    is_visible         BOOLEAN     NULL DEFAULT TRUE,
    designation_de     VARCHAR(30) NOT NULL,
    designation_fr     VARCHAR(30) NULL,
    designation_it     VARCHAR(30) NULL,
    designation_en     VARCHAR(30) NULL,
    abbreviation_de    VARCHAR(10) NULL,
    abbreviation_fr    VARCHAR(10) NULL,
    abbreviation_it    VARCHAR(10) NULL,
    abbreviation_en    VARCHAR(10) NULL,
    is_review_required BOOLEAN     NULL DEFAULT FALSE,
    creation_date      TIMESTAMP   NOT NULL,
    creator            VARCHAR(50) NOT NULL,
    edition_date       TIMESTAMP   NOT NULL,
    editor             VARCHAR(50) NOT NULL
);

CREATE SEQUENCE stop_place_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Stop Place HALTESTELLE
-----------------------------------------------------------------------------------------

CREATE TABLE stop_place
(
    id                       BIGINT      NULL PRIMARY KEY,
    service_point_version_id BIGINT      NOT NULL,
    stop_place_type_id       SMALLINT    NULL,
    means_of_transport       VARCHAR(50) NULL,
    creation_date            TIMESTAMP   NOT NULL,
    creator                  VARCHAR(50) NOT NULL,
    edition_date             TIMESTAMP   NOT NULL,
    editor                   VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version_id
        FOREIGN KEY (service_point_version_id)
            REFERENCES service_point_version (id),
    CONSTRAINT fk_stop_place_type_id
        FOREIGN KEY (stop_place_type_id)
            REFERENCES stop_place_type (id)
);

CREATE SEQUENCE stop_place_seq START WITH 1000 INCREMENT BY 1;
