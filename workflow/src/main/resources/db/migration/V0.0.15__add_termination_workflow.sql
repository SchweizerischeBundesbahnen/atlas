CREATE SEQUENCE termination_stop_point_workflow_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE termination_stop_point_workflow
(
    id                         BIGINT      NOT NULL PRIMARY KEY,
    version_id                 BIGINT,
    sloid                      VARCHAR(50),
    sboid                      VARCHAR(32),
    status                     VARCHAR(50),
    applicant_mail             VARCHAR(255),
    workflow_comment           VARCHAR(1500),
    bo_termination_date        TIMESTAMP   NOT NULL,
    info_plus_termination_date TIMESTAMP   NOT NULL,
    nova_termination_date      TIMESTAMP   NOT NULL,
    creation_date              TIMESTAMP   NOT NULL,
    creator                    VARCHAR(50) NOT NULL,
    edition_date               TIMESTAMP   NOT NULL,
    editor                     VARCHAR(50) NOT NULL,
    info_plus_person_id        BIGINT,
    nova_person_id             BIGINT,

    CONSTRAINT fk_info_plus_person
        FOREIGN KEY (info_plus_person_id)
            REFERENCES person (id),

    CONSTRAINT fk_nova_person
        FOREIGN KEY (nova_person_id)
            REFERENCES person (id)
);