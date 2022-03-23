-----------------------------------------------------------------------------------------
-- Timetable Field Number
-----------------------------------------------------------------------------------------

CREATE TABLE timetable_field_number_version
(
    id                           BIGINT       NOT NULL PRIMARY KEY,
    ttfnid                       VARCHAR(500) NOT NULL,
    description                  VARCHAR(255),
    number                       VARCHAR(50)  NOT NULL,
    swiss_timetable_field_number VARCHAR(50)  NOT NULL,
    status                       VARCHAR(50)  NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    business_organisation        VARCHAR(50)  NOT NULL,
    comment                      VARCHAR(1500),
    version                      BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE timetable_field_number_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE timetable_field_line_relation
(
    id                         BIGINT NOT NULL PRIMARY KEY,
    slnid                      VARCHAR(500),
    timetable_field_version_id BIGINT,
    CONSTRAINT fk_timetable_field_number_version
        FOREIGN KEY (timetable_field_version_id)
            REFERENCES timetable_field_number_version (id)
);

CREATE SEQUENCE timetable_field_line_relation_seq START WITH 1000 INCREMENT BY 1;

CREATE SEQUENCE ttfnid_seq START WITH 1000000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- LiDi
-----------------------------------------------------------------------------------------

CREATE SEQUENCE slnid_seq START WITH 1000000 INCREMENT BY 1;

CREATE TABLE coverage
(
    id                    BIGINT NOT NULL PRIMARY KEY,
    slnid                 VARCHAR(500),
    model_type            VARCHAR(50),
    valid_from            DATE,
    valid_to              DATE,
    coverage_type         VARCHAR(50),
    validation_error_type VARCHAR(1000)
);

CREATE SEQUENCE coverage_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Line
-----------------------------------------------------------------------------------------

CREATE TABLE line_version
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    swiss_line_number     VARCHAR(50)  NOT NULL,
    slnid                 VARCHAR(500) NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    line_type             VARCHAR(50)  NOT NULL,
    payment_type          VARCHAR(50)  NOT NULL,
    number                VARCHAR(50),
    alternative_name      VARCHAR(50),
    combination_name      VARCHAR(50),
    long_name             VARCHAR(255),
    color_font_rgb        VARCHAR(50)  NOT NULL,
    color_back_rgb        VARCHAR(50)  NOT NULL,
    color_font_cmyk       VARCHAR(50)  NOT NULL,
    color_back_cmyk       VARCHAR(50)  NOT NULL,
    icon                  VARCHAR(255),
    description           VARCHAR(255),
    valid_from            DATE         NOT NULL,
    valid_to              DATE         NOT NULL,
    business_organisation VARCHAR(50)  NOT NULL,
    comment               VARCHAR(1500),
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE line_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE line_version
    ADD CONSTRAINT line_slnid_unique UNIQUE (slnid, valid_from);

-----------------------------------------------------------------------------------------
-- Subline
-----------------------------------------------------------------------------------------

CREATE TABLE subline_version
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    swiss_subline_number  VARCHAR(50)  NOT NULL,
    mainline_slnid        VARCHAR(500),
    slnid                 VARCHAR(500) NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    subline_type          VARCHAR(50)  NOT NULL,
    description           VARCHAR(255),
    number                VARCHAR(50),
    long_name             VARCHAR(255),
    payment_type          VARCHAR(50)  NOT NULL,
    valid_from            DATE         NOT NULL,
    valid_to              DATE         NOT NULL,
    business_organisation VARCHAR(50)  NOT NULL,
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE subline_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE subline_version
    ADD CONSTRAINT subline_slnid_unique UNIQUE (slnid, valid_from);