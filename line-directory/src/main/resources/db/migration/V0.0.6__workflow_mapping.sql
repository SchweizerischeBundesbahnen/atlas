CREATE TABLE line_version_workflow
(
    id              BIGINT NOT NULL PRIMARY KEY,
    line_version_id BIGINT,
    workflow_id     BIGINT,
    CONSTRAINT fk_line_version
        FOREIGN KEY (line_version_id)
            REFERENCES line_version (id)
);

CREATE SEQUENCE line_version_workflow_seq START WITH 1000 INCREMENT BY 1;

