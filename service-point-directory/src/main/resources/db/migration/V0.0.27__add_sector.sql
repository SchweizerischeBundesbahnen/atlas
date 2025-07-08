CREATE TABLE sector_group_version
(
    id                           BIGINT PRIMARY KEY,
    sloid                        VARCHAR(128) NOT NULL,
    traffic_point_sloid          VARCHAR(128) NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    designation                  VARCHAR(8)   NOT NULL,
    length                       NUMERIC(6,3),
    status                       VARCHAR(50)  NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT sector_group_version_sloid_unique UNIQUE (sloid, valid_from)
);

CREATE SEQUENCE sector_group_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE sector_version
(
    id                           BIGINT PRIMARY KEY,
    sloid                        VARCHAR(128) NOT NULL,
    traffic_point_sloid          VARCHAR(128) NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    designation                  VARCHAR(8)   NOT NULL,
    north                        NUMERIC(19, 11) NOT NULL,
    east                         NUMERIC(19, 11) NOT NULL,
    height                       NUMERIC(10, 5),
    spatial_reference            VARCHAR(50) NOT NULL,
    length                       NUMERIC(6,3),
    edge_height                  NUMERIC(3,0),
    status                       VARCHAR(50)  NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT sector_version_sloid_unique
        UNIQUE (sloid, valid_from)
);

CREATE SEQUENCE sector_version_seq START WITH 1000 INCREMENT BY 1;


CREATE TABLE sector_group_relations
(
    sector_sloid                 VARCHAR(128) NOT NULL,
    sector_group_sloid           VARCHAR(128) NOT NULL,

    CONSTRAINT pk_sector_group_relations
        PRIMARY KEY (sector_sloid, sector_group_sloid)

);
