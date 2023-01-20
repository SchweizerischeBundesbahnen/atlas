-----------------------------------------------------------------------------------------
-- Service Point Version Geolocation - DIENSTSTELLEN_FC
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version_geolocation
(
    id                        BIGINT PRIMARY KEY,
    spatial_reference         VARCHAR(50)     NOT NULL,
    east                      NUMERIC(19, 11) NULL,
    north                     NUMERIC(19, 11) NULL,
    height                    NUMERIC(6, 2)   NULL,
    country                   VARCHAR(50)     NULL,
    swiss_canton              VARCHAR(50)     NULL,
    swiss_district_name       VARCHAR(255)    NULL,
    swiss_district_number     SMALLINT        NULL,
    swiss_municipality_number SMALLINT        NULL,
    swiss_municipality_name   VARCHAR(255)    NULL,
    swiss_locality_name       VARCHAR(255)    NULL,
    creation_date             TIMESTAMP       NOT NULL,
    creator                   VARCHAR(50)     NOT NULL,
    edition_date              TIMESTAMP       NOT NULL,
    editor                    VARCHAR(50)     NOT NULL,
    version                   BIGINT          NOT NULL DEFAULT 0
);

CREATE INDEX service_point_version_geolocation_spatial_reference_index
    ON service_point_version_geolocation (spatial_reference);
CREATE INDEX service_point_version_geolocation_east_north_index
    ON service_point_version_geolocation (east, north);



CREATE SEQUENCE service_point_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Service Point Version - DIENSTSTELLEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version
(
    id                               BIGINT PRIMARY KEY,
    service_point_geolocation_id     BIGINT      NULL,
    number                           INTEGER     NOT NULL,
    sloid                            VARCHAR(500),
    number_short                     INTEGER     NOT NULL,
    country                          VARCHAR(50) NOT NULL,
    designation_long                 VARCHAR(50) NULL,
    designation_official             VARCHAR(30) NOT NULL,
    abbreviation                     VARCHAR(6)  NULL,
    status_didok3                    VARCHAR(50) NOT NULL,
    sort_code_of_destination_station VARCHAR(10) NULL,
    business_organisation            VARCHAR(50) NOT NULL,
    operating_point_type             VARCHAR(50),
    stop_point_type                  VARCHAR(50),
    status                           VARCHAR(50) NOT NULL,
    operating_point_kilometer_master INTEGER,
    operating_point_route_network    BOOLEAN     NOT NULL DEFAULT FALSE,
    comment                          VARCHAR(1500),
    valid_from                       DATE        NOT NULL,
    valid_to                         DATE        NOT NULL,
    creation_date                    TIMESTAMP   NOT NULL,
    creator                          VARCHAR(50) NOT NULL,
    edition_date                     TIMESTAMP   NOT NULL,
    editor                           VARCHAR(50) NOT NULL,
    version                          BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT fk_service_point_geolocation_id
        FOREIGN KEY (service_point_geolocation_id)
            REFERENCES service_point_version_geolocation (id)
);

CREATE INDEX service_point_version_valid_from_to_index
    ON service_point_version (valid_from, valid_to);

CREATE SEQUENCE service_point_version_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE service_point_version
    ADD CONSTRAINT spv_number_unique UNIQUE (number, valid_from);

-----------------------------------------------------------------------------------------
-- Service Point Category DS_KATEGORIEN
-----------------------------------------------------------------------------------------

CREATE TABLE service_point_version_categories
(
    service_point_version_id BIGINT      NOT NULL,
    categories               VARCHAR(50) NOT NULL
);

-----------------------------------------------------------------------------------------
-- Loading Point Version Geolocation - LADESTELLEN_FC
-----------------------------------------------------------------------------------------

CREATE TABLE loading_point_version_geolocation
(
    id                BIGINT PRIMARY KEY,
    spatial_reference VARCHAR(50)     NOT NULL,
    east              NUMERIC(19, 11) NULL,
    north             NUMERIC(19, 11) NULL,
    height            NUMERIC(6, 2)   NULL,
    creation_date     TIMESTAMP       NOT NULL,
    creator           VARCHAR(50)     NOT NULL,
    edition_date      TIMESTAMP       NOT NULL,
    editor            VARCHAR(50)     NOT NULL,
    version           BIGINT          NOT NULL DEFAULT 0
);

CREATE SEQUENCE loading_point_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Loading Point LADESTELLEN
-----------------------------------------------------------------------------------------

CREATE TABLE loading_point_version
(
    id                           BIGINT PRIMARY KEY,
    number                       BIGINT      NOT NULL,
    designation                  VARCHAR(12) NOT NULL,
    designation_long             VARCHAR(35),
    connection_point             BOOLEAN     NOT NULL,
    service_point_number         INTEGER     NOT NULL,
    valid_from                   DATE        NOT NULL,
    valid_to                     DATE        NOT NULL,
    loading_point_geolocation_id BIGINT      NULL,
    creation_date                TIMESTAMP   NOT NULL,
    creator                      VARCHAR(50) NOT NULL,
    edition_date                 TIMESTAMP   NOT NULL,
    editor                       VARCHAR(50) NOT NULL,
    version                      BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT fk_loading_point_version_geolocation_id
        FOREIGN KEY (loading_point_geolocation_id)
            REFERENCES loading_point_version_geolocation (id)
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
    id                BIGINT PRIMARY KEY,
    spatial_reference VARCHAR(50)     NOT NULL,
    east              NUMERIC(19, 11) NULL,
    north             NUMERIC(19, 11) NULL,
    height            NUMERIC(6, 2)   NULL,
    creation_date     TIMESTAMP       NOT NULL,
    creator           VARCHAR(50)     NOT NULL,
    edition_date      TIMESTAMP       NOT NULL,
    editor            VARCHAR(50)     NOT NULL,
    version           BIGINT          NOT NULL DEFAULT 0
);

CREATE SEQUENCE traffic_point_element_version_geolocation_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Traffic Point Element - VERKEHRSPUNKT_ELEMENTE
-----------------------------------------------------------------------------------------

CREATE TABLE traffic_point_element_version
(
    id                           BIGINT PRIMARY KEY,
    sloid                        VARCHAR(500) NOT NULL,
    parent_sloid                 VARCHAR(500),
    designation                  VARCHAR(50),
    designation_operational      VARCHAR(50),
    traffic_point_element_type   VARCHAR(50),
    length                       NUMERIC(13, 2),
    boarding_area_height         NUMERIC(5, 2), -- 0-100cm
    compass_direction            NUMERIC(5, 2), -- 0-360.00
    service_point_number         BIGINT       NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    traffic_point_geolocation_id BIGINT       NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_traffic_point_element_version_geolocation_id
        FOREIGN KEY (traffic_point_geolocation_id)
            REFERENCES traffic_point_element_version_geolocation (id)
);

CREATE SEQUENCE traffic_point_element_version_seq START WITH 1000 INCREMENT BY 1;

