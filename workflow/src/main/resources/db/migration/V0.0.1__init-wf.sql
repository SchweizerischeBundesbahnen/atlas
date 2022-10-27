CREATE TABLE person
(
    id            BIGINT    NOT NULL PRIMARY KEY,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    function      VARCHAR(50),
    mail          VARCHAR(255),
    creation_date TIMESTAMP NOT NULL,
    edition_date  TIMESTAMP NOT NULL

);

CREATE SEQUENCE person_seq START WITH 1000 INCREMENT BY 1;


CREATE TABLE workflow
(
    id                 BIGINT    NOT NULL PRIMARY KEY,
    business_object_id BIGINT,
    workflow_type      VARCHAR(50),
    swiss_id           VARCHAR(50),
    description        VARCHAR(50),
    status             VARCHAR(50),
    workflow_comment   VARCHAR(1500),
    check_comment      VARCHAR(1500),
    client_id          BIGINT,
    examinant_id       BIGINT,
    creation_date      TIMESTAMP NOT NULL,
    edition_date       TIMESTAMP NOT NULL,
    CONSTRAINT fk_client
        FOREIGN KEY (client_id)
            REFERENCES person (id),
    CONSTRAINT fk_examinant
        FOREIGN KEY (examinant_id)
            REFERENCES person (id)
);

CREATE SEQUENCE workflow_seq START WITH 1000 INCREMENT BY 1;

