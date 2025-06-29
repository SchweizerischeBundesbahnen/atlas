-- rename lineWorkflow to line-lineWorkflow table
ALTER TABLE workflow
    RENAME to line_workflow;

-- ALTER SEQUENCE workflow_seq RENAME to line_workflow_seq; NOT SUPPORTED FROM H2
DROP SEQUENCE workflow_seq;
CREATE SEQUENCE line_workflow_seq START WITH 2000 INCREMENT BY 1;

CREATE TABLE stop_point_workflow
(
    id                      BIGINT      NOT NULL PRIMARY KEY,
    version_id              BIGINT,
    sloid                   VARCHAR(50),
    sboid                   VARCHAR(32),
    swiss_municipality_name VARCHAR(255),
    status                  VARCHAR(50),
    workflow_comment        VARCHAR(1500),
    designation_official    VARCHAR(30) NOT NULL,
    start_date              DATE        NOT NULL,
    end_date                DATE,
    creation_date           TIMESTAMP   NOT NULL,
    creator                 VARCHAR(50) NOT NULL,
    edition_date            TIMESTAMP   NOT NULL,
    editor                  VARCHAR(50) NOT NULL,
    follow_up_workflow_id   BIGINT,

    CONSTRAINT fk_follow_up_workflow
        FOREIGN KEY (follow_up_workflow_id)
            REFERENCES stop_point_workflow (id)
);

CREATE TABLE stop_point_workflow_cc_emails
(
    stop_point_workflow_id BIGINT       NOT NULL,
    cc_emails              VARCHAR(255) NOT NULL,
    FOREIGN KEY (stop_point_workflow_id) REFERENCES stop_point_workflow (id)
);

CREATE SEQUENCE stop_point_workflow_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE decision
(
    id                  BIGINT      NOT NULL PRIMARY KEY,
    judgement           VARCHAR(50),
    decision_type       VARCHAR(50),
    motivation          VARCHAR(1500),
    examinant_id        BIGINT,
    motivation_date     TIMESTAMP,
    fot_judgement       VARCHAR(50),
    fot_motivation      VARCHAR(1500),
    fot_motivation_date TIMESTAMP,
    fot_overrider_id    BIGINT,
    creation_date       TIMESTAMP   NOT NULL,
    creator             VARCHAR(50) NOT NULL,
    edition_date        TIMESTAMP   NOT NULL,
    editor              VARCHAR(50) NOT NULL,

    CONSTRAINT fk_examinant_decision_bav
        FOREIGN KEY (examinant_id)
            REFERENCES person (id),

    CONSTRAINT fk_for_overrider
        FOREIGN KEY (fot_overrider_id)
            REFERENCES person (id)
);

CREATE SEQUENCE decision_seq START WITH 1000 INCREMENT BY 1;


CREATE TABLE otp
(
    id            BIGINT     NOT NULL PRIMARY KEY,
    person_id     BIGINT,
    code          VARCHAR(6) NOT NULL,
    creation_time TIMESTAMP  NOT NULL,

    CONSTRAINT fk_person
        FOREIGN KEY (person_id)
            REFERENCES person (id)
);

CREATE SEQUENCE otp_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE person
    ADD stop_point_workflow_id BIGINT DEFAULT NULL;

ALTER TABLE person
    ADD foreign key (stop_point_workflow_id) references
        stop_point_workflow (id);

ALTER TABLE person
    ADD creator VARCHAR(50) NOT NULL default 'atlas';

ALTER TABLE person
    ADD editor VARCHAR(50) NOT NULL default 'atlas';