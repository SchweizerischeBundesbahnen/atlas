CREATE TABLE line_version_snapshot
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
    version               BIGINT       NOT NULL DEFAULT 0,
    workflow_id           BIGINT       NOT NULL,
    workflow_status       VARCHAR(50)  NOT NULL,
    parent_object_id      BIGINT       NOT NULL
);

CREATE SEQUENCE line_version_snapshot_seq START WITH 1000 INCREMENT BY 1;