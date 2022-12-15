-----------------------------------------------------------------------------------------
-- Service Point Version - DIENSTSTELLEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version
(
    id                               BIGINT PRIMARY KEY,
    number                           INTEGER     NOT NULL,
    sloid                            VARCHAR(500),
    check_digit                      SMALLINT    NOT NULL,
    number_short                     INTEGER     NOT NULL,
    country                          VARCHAR(50) NOT NULL,
    designation_long                 VARCHAR(50) NULL,
    designation_official             VARCHAR(30) NOT NULL,
    abbreviation                     VARCHAR(6)  NULL,
    status_didok3                    VARCHAR(50)    NULL,
    sort_code_of_destination_station VARCHAR(10) NULL,
    business_organisation            VARCHAR(50) NOT NULL,
    has_geolocation                  BOOLEAN     NULL     DEFAULT FALSE,
    operating_point_type             VARCHAR(50),
    stop_place_type                  VARCHAR(50),
    status                           VARCHAR(50) NOT NULL,
    valid_from                       DATE        NOT NULL,
    valid_to                         DATE        NOT NULL,
    creation_date                    TIMESTAMP   NOT NULL,
    creator                          VARCHAR(50) NOT NULL,
    edition_date                     TIMESTAMP   NOT NULL,
    editor                           VARCHAR(50) NOT NULL,
    version                          BIGINT      NOT NULL DEFAULT 0
);

CREATE SEQUENCE service_point_version_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE service_point_version
    ADD CONSTRAINT spv_number_unique UNIQUE (number, valid_from);

-- search api field indexes
CREATE INDEX spv_number_idx ON service_point_version (number);
CREATE INDEX spv_numbershort_idx ON service_point_version (number_short);
CREATE INDEX spv_designation_idx ON service_point_version (designation_long);
CREATE INDEX spv_designationlong_idx ON service_point_version (designation_official);
CREATE INDEX spv_abbrevation_idx ON service_point_version (abbreviation);
CREATE INDEX spv_status_didok3_idx ON service_point_version (status_didok3);
CREATE INDEX spv_said_idx ON service_point_version (business_organisation);
CREATE INDEX spv_geolocation_idx ON service_point_version (has_geolocation);
CREATE INDEX spv_validity_idx ON service_point_version (valid_from, valid_to);
CREATE INDEX spv_creation_date_idx ON service_point_version (creation_date);
CREATE INDEX spv_edition_date_idx ON service_point_version (edition_date);

-----------------------------------------------------------------------------------------
-- Service Point Version Geolocation - DIENSTSTELLEN_FC
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version_geolocation
(
    id                       BIGINT PRIMARY KEY,
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
    version                  BIGINT       NOT NULL DEFAULT 0,
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
    id                   BIGINT PRIMARY KEY,
    service_point_number INTEGER       NOT NULL,
    comment              VARCHAR(1500) NOT NULL,
    creation_date        TIMESTAMP     NOT NULL,
    creator              VARCHAR(50)   NOT NULL,
    edition_date         TIMESTAMP     NOT NULL,
    editor               VARCHAR(50)   NOT NULL,
    version              BIGINT        NOT NULL DEFAULT 0
);

CREATE SEQUENCE service_point_comment_seq START WITH 1000 INCREMENT BY 1;


-----------------------------------------------------------------------------------------
-- Service Point Category DS_KATEGORIEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version_categories
(
    service_point_version_id BIGINT      NOT NULL,
    categories               VARCHAR(50) NOT NULL
);

-----------------------------------------------------------------------------------------
-- Loading Point LADESTELLEN
-----------------------------------------------------------------------------------------

CREATE TABLE loading_point_version
(
    id                   BIGINT PRIMARY KEY,
    number               BIGINT      NOT NULL,
    designation          VARCHAR(12) NOT NULL,
    designation_long     VARCHAR(35),
    connection_point     BOOLEAN     NOT NULL,
    service_point_number BIGINT      NOT NULL,
    valid_from           DATE        NOT NULL,
    valid_to             DATE        NOT NULL,
    creation_date        TIMESTAMP   NOT NULL,
    creator              VARCHAR(50) NOT NULL,
    edition_date         TIMESTAMP   NOT NULL,
    editor               VARCHAR(50) NOT NULL,
    version              BIGINT      NOT NULL DEFAULT 0
);

CREATE SEQUENCE loading_point_version_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Service Point - MeansOfTransport - Verkehrsmittel
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version_means_of_transport
(
    service_point_version_id BIGINT      NOT NULL,
    means_of_transport       VARCHAR(50) NOT NULL
);

-----------------------------------------------------------------------------------------
-- Traffic Point Version Geolocation - VERKEHRSPUNKT_ELEMENTE_FC
-----------------------------------------------------------------------------------------

CREATE TABLE traffic_point_element_version_geolocation
(
    id                               BIGINT PRIMARY KEY,
    source_spatial_ref               INTEGER      NOT NULL,
    e_lv03                           NUMERIC      NULL,
    n_lv03                           NUMERIC      NULL,
    e_lv95                           NUMERIC      NULL,
    n_lv95                           NUMERIC      NULL,
    e_wgs84                          NUMERIC      NULL,
    n_wgs84                          NUMERIC      NULL,
    height                           NUMERIC      NULL,
    iso_country_code                 VARCHAR(2)   NULL,
    swiss_canton_fso_number          SMALLINT     NULL,
    swiss_canton_name                VARCHAR(50)  NULL,
    swiss_canton_number              SMALLINT     NULL,
    swiss_district_name              VARCHAR(255) NULL,
    swiss_district_number            SMALLINT     NULL,
    swiss_municipality_name          VARCHAR(255) NULL,
    swiss_locality_name              VARCHAR(255) NULL,
    creation_date                    TIMESTAMP    NOT NULL,
    creator                          VARCHAR(50)  NOT NULL,
    edition_date                     TIMESTAMP    NOT NULL,
    editor                           VARCHAR(50)  NOT NULL,
    version                          BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE traffic_point_element_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Traffic Point Element - VERKEHRSPUNKT_ELEMENTE
-----------------------------------------------------------------------------------------

CREATE TABLE traffic_point_element_version
(
    id                         BIGINT PRIMARY KEY,
    sloid                      VARCHAR(500) NOT NULL,
    parent_sloid               VARCHAR(500),
    designation                VARCHAR(50),
    designation_operational    VARCHAR(50),
    traffic_point_element_type VARCHAR(50),
    length                     BIGINT,
    boarding_area_height       BIGINT,
    compass_direction          BIGINT,
    service_point_number       BIGINT       NOT NULL,
    valid_from                 DATE         NOT NULL,
    valid_to                   DATE         NOT NULL,
    geolocation_id             BIGINT,
    creation_date              TIMESTAMP    NOT NULL,
    creator                    VARCHAR(50)  NOT NULL,
    edition_date               TIMESTAMP    NOT NULL,
    editor                     VARCHAR(50)  NOT NULL,
    version                    BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_traffic_point_element_version_geolocation_id
        FOREIGN KEY (geolocation_id)
            REFERENCES traffic_point_element_version_geolocation (id)
);

CREATE SEQUENCE traffic_point_element_version_seq START WITH 1000 INCREMENT BY 1;