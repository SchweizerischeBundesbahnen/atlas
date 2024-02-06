DROP TABLE information_desk_version;
DROP SEQUENCE information_desk_version_seq;

DROP TABLE ticket_counter_version;
DROP SEQUENCE ticket_counter_version_seq;

CREATE TABLE contact_point_version
(
    id                              BIGINT       NOT NULL PRIMARY KEY,
    sloid                           VARCHAR(500) NOT NULL,
    number                          INTEGER      NOT NULL,
    parent_service_point_sloid      VARCHAR(500) NOT NULL,
    designation                     VARCHAR(50),
    additional_information          VARCHAR(2000),
    induction_loop                  VARCHAR(50),
    opening_hours                   VARCHAR(2000),
    wheelchair_access               VARCHAR(50),
    type                            VARCHAR(50)  NOT NULL,
    valid_from                      DATE         NOT NULL,
    valid_to                        DATE         NOT NULL,
    creation_date                   TIMESTAMP    NOT NULL,
    creator                         VARCHAR(50)  NOT NULL,
    edition_date                    TIMESTAMP    NOT NULL,
    editor                          VARCHAR(50)  NOT NULL,
    version                         BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE contact_point_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE contact_point_version
    ADD CONSTRAINT contact_point_sloid_unique UNIQUE (sloid, valid_from);