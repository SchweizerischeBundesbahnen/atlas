-----------------------------------------------------------------------------------------
-- Service Point
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
    abbreviation         VARCHAR(6) NULL,
    status_didok         SMALLINT    NOT NULL,
    said                 VARCHAR(38) NULL,
    has_geolocation      BOOLEAN NULL DEFAULT FALSE,
    valid_from           DATE        NOT NULL,
    valid_to             DATE        NOT NULL,
    creation_date        TIMESTAMP   NOT NULL,
    creator              VARCHAR(50) NOT NULL,
    edition_date         TIMESTAMP   NOT NULL,
    editor               VARCHAR(50) NOT NULL,
    version              BIGINT      NOT NULL DEFAULT 0
);

CREATE SEQUENCE service_point_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE service_point_version_geolocation
(
    id                      BIGINT      NOT NULL PRIMARY KEY,
    service_point_version   BIGINT      NOT NULL,
    source_spatial_ref      INTEGER     NOT NULL,
    e_lv03                  NUMERIC NULL,
    n_lv03                  NUMERIC NULL,
    e_lv95                  NUMERIC NULL,
    n_lv95                  NUMERIC NULL,
    e_wgs84                 NUMERIC NULL,
    n_wgs84                 NUMERIC NULL,
    height                  NUMERIC NULL,
    country_code            VARCHAR(2) NULL,
    swiss_canton_fso_number SMALLINT NULL,
    swiss_canton_name       VARCHAR(50) NULL,
    swiss_canton_number     SMALLINT NULL,
    swiss_district_name     VARCHAR(255) NULL,
    swiss_district_number   SMALLINT NULL,
    swiss_municipality_name VARCHAR(255) NULL,
    swiss_locality_name     VARCHAR(255) NULL,
    creation_date           TIMESTAMP   NOT NULL,
    creator                 VARCHAR(50) NOT NULL,
    edition_date            TIMESTAMP   NOT NULL,
    editor                  VARCHAR(50) NOT NULL,
    CONSTRAINT fk_service_point_version
        FOREIGN KEY (service_point_version)
            REFERENCES service_point_version (version)
);

CREATE SEQUENCE service_point_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

