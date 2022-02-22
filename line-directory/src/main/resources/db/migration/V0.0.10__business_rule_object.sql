CREATE TABLE subline_coverage
(
    id                    BIGINT NOT NULL PRIMARY KEY,
    slnid                 VARCHAR(500),
    model_type             VARCHAR(500),
    subline_coverage_type VARCHAR(50),
    validation_error_type VARCHAR(1000)
);

CREATE SEQUENCE subline_coverage_seq START WITH 1000 INCREMENT BY 1;

