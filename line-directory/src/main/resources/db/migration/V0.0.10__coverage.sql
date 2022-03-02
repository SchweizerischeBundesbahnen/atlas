CREATE TABLE coverage
(
    id                    BIGINT NOT NULL PRIMARY KEY,
    slnid                 VARCHAR(500),
    model_type            VARCHAR(500),
    valid_from            DATE,
    valid_to              DATE,
    coverage_type VARCHAR(50),
    validation_error_type VARCHAR(1000)
);

CREATE SEQUENCE coverage_seq START WITH 1000 INCREMENT BY 1;

