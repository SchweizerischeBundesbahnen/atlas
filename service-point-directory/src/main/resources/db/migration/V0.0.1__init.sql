-----------------------------------------------------------------------------------------
-- Service Point Version
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version
(
    id                   BIGINT       NOT NULL PRIMARY KEY,
    sloid                VARCHAR(500) NOT NULL,
    number               INTEGER      NOT NULL,
    check_digit          SMALLINT     NOT NULL,
    number_short         INTEGER      NOT NULL,
    uic_country_code     SMALLINT     NOT NULL,
    designation_long     VARCHAR(50)  NULL,
    designation_official VARCHAR(30)  NOT NULL,
    abbreviation         VARCHAR(6)   NULL,
    status_didok         SMALLINT     NOT NULL,
    said                 VARCHAR(38)  NULL,
    has_geolocation      BOOLEAN      NULL     DEFAULT FALSE,
    valid_from           DATE         NOT NULL,
    valid_to             DATE         NOT NULL,
    creation_date        TIMESTAMP    NOT NULL,
    creator              VARCHAR(50)  NOT NULL,
    edition_date         TIMESTAMP    NOT NULL,
    editor               VARCHAR(50)  NOT NULL,
    version              BIGINT       NOT NULL DEFAULT 0
);

--ALTER TABLE service_point_version ADD CONSTRAINT service_point_version_unique UNIQUE (version, valid_from);

CREATE SEQUENCE service_point_version_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE service_point_version
    ADD CONSTRAINT spv_sloid_unique UNIQUE (sloid, valid_from);

-- search api field indexes
CREATE INDEX spv_number_idx ON service_point_version (number);
CREATE INDEX spv_numbershort_idx ON service_point_version (number_short);
CREATE INDEX spv_countrycode_idx ON service_point_version (uic_country_code);
CREATE INDEX spv_designation_idx ON service_point_version (designation_long);
CREATE INDEX spv_designationlong_idx ON service_point_version (designation_official);
CREATE INDEX spv_abbrevation_idx ON service_point_version (abbreviation);
CREATE INDEX spv_status_idx ON service_point_version (status_didok);
CREATE INDEX spv_said_idx ON service_point_version (said);
CREATE INDEX spv_geolocation_idx ON service_point_version (has_geolocation);
CREATE INDEX spv_validity_idx ON service_point_version (valid_from, valid_to);
CREATE INDEX spv_creation_date_idx ON service_point_version (creation_date);
CREATE INDEX spv_edition_date_idx ON service_point_version (edition_date);

-----------------------------------------------------------------------------------------
-- Service Point Version Geolocation
-----------------------------------------------------------------------------------------


CREATE TABLE service_point_version_geolocation
(
    id                      BIGINT       NOT NULL PRIMARY KEY,
    service_point_version   BIGINT       NOT NULL,
    source_spatial_ref      INTEGER      NOT NULL,
    e_lv03                  NUMERIC      NULL,
    n_lv03                  NUMERIC      NULL,
    e_lv95                  NUMERIC      NULL,
    n_lv95                  NUMERIC      NULL,
    e_wgs84                 NUMERIC      NULL,
    n_wgs84                 NUMERIC      NULL,
    height                  NUMERIC      NULL,
    country_code            VARCHAR(2)   NULL,
    swiss_canton_fso_number SMALLINT     NULL,
    swiss_canton_name       VARCHAR(50)  NULL,
    swiss_canton_number     SMALLINT     NULL,
    swiss_district_name     VARCHAR(255) NULL,
    swiss_district_number   SMALLINT     NULL,
    swiss_municipality_name VARCHAR(255) NULL,
    swiss_locality_name     VARCHAR(255) NULL,
    creation_date           TIMESTAMP    NOT NULL,
    creator                 VARCHAR(50)  NOT NULL,
    edition_date            TIMESTAMP    NOT NULL,
    editor                  VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_service_point_version
        FOREIGN KEY (service_point_version)
            REFERENCES service_point_version (version)
);

CREATE SEQUENCE service_point_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-- search api field indexes
CREATE INDEX spvgeo_spatialref_idx ON service_point_version_geolocation (source_spatial_ref);
CREATE INDEX spvgeo_coordlv03_idx ON service_point_version_geolocation (e_lv03, n_lv03);
CREATE INDEX spvgeo_coordlv95_idx ON service_point_version_geolocation (e_lv95, n_lv95);
CREATE INDEX spvgeo_coordwgs84_idx ON service_point_version_geolocation (e_wgs84, n_wgs84);
CREATE INDEX spvgeo_countrycode_idx ON service_point_version_geolocation (country_code);
