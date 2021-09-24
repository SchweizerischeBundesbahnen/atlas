CREATE TABLE line_version
(
    id                    BIGINT NOT NULL PRIMARY KEY,
    status                VARCHAR(50),
    type                  VARCHAR(50),
    slnid                 VARCHAR(500),
    payment_type          VARCHAR(50),
    short_name            VARCHAR(50),
    alternative_name      VARCHAR(50),
    combination_name      VARCHAR(500),
    long_name             VARCHAR(1000),
    color_font_rgb        VARCHAR(50),
    color_back_rgb        VARCHAR(50),
    color_font_cmyk       VARCHAR(50),
    color_back_cmyk       VARCHAR(50),
    icon                  VARCHAR(255),
    description           VARCHAR(500),
    valid_from            DATE,
    valid_to              DATE,
    business_organisation VARCHAR(50),
    comment               VARCHAR(250),
    swiss_line_number     VARCHAR(50),
    creation_date         TIMESTAMP,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP,
    editor                VARCHAR(50)
);

CREATE SEQUENCE line_version_seq START WITH 1000 INCREMENT BY 1;

CREATE SEQUENCE subline_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE subline_version
(
    id                    BIGINT NOT NULL PRIMARY KEY,
    line_version_id       BIGINT,
    type                  VARCHAR(50),
    slnid                 VARCHAR(500),
    description           VARCHAR(500),
    short_name            VARCHAR(50),
    long_name             VARCHAR(1000),
    payment_type          VARCHAR(50),
    valid_from            DATE,
    valid_to              DATE,
    business_organisation VARCHAR(50),
    creation_date         TIMESTAMP,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP,
    editor                VARCHAR(50),
    CONSTRAINT fk_line_version
        FOREIGN KEY (line_version_id)
            REFERENCES line_version (id)
);
