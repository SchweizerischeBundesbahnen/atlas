CREATE TABLE timetable_field_number_version
(
    id                              BIGINT NOT NULL PRIMARY KEY,
    ttfnid                          VARCHAR(500),
    name                            VARCHAR(1000),
    number                          VARCHAR(50),
    swiss_timetable_field_number    VARCHAR(50),
    status                          VARCHAR(50),
    creation_date                   TIMESTAMP,
    creator                         VARCHAR(50),
    edition_date                    TIMESTAMP,
    editor                          VARCHAR(50),
    valid_from                      DATE,
    valid_to                        DATE,
    business_organisation           VARCHAR(50),
    comment                         VARCHAR(250),
    type      VARCHAR(50),
    name_compact                    VARCHAR(50)
);

CREATE SEQUENCE timetable_field_number_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE timetable_field_line_relation
(
    id                              BIGINT NOT NULL PRIMARY KEY,
    slnid                           VARCHAR(500),
    timetable_field_version_id      BIGINT,
    CONSTRAINT fk_timetable_field_number_version
        FOREIGN KEY (timetable_field_version_id)
            REFERENCES timetable_field_number_version(id)
);

CREATE SEQUENCE timetable_field_line_relation_seq START WITH 1000 INCREMENT BY 1;