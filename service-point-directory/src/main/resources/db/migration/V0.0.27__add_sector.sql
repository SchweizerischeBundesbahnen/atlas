CREATE TABLE sector_group
(
    id                           BIGINT PRIMARY KEY,
    sloid                        VARCHAR(128) NOT NULL,
    traffic_point_sloid          VARCHAR(128) NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    designation                  VARCHAR(8)   NOT NULL,
    length                       NUMERIC(6,3),
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0,
    UNIQUE (sloid, valid_from)
);

CREATE SEQUENCE sector_group_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE sector
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
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0,
    UNIQUE (sloid, valid_from)
);

CREATE SEQUENCE sector_seq START WITH 1000 INCREMENT BY 1;


CREATE TABLE sector_group_rel
(
    id                           BIGINT PRIMARY KEY,
    sector_sloid                 VARCHAR(128) NOT NULL,
    sector_group_sloid           VARCHAR(128) NOT NULL,
    UNIQUE (sector_sloid)
);

CREATE SEQUENCE sector_group_rel_seq START WITH 1000 INCREMENT BY 1;
