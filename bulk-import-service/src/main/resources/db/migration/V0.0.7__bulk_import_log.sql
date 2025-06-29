CREATE TABLE bulk_import_log
(
    id               BIGINT PRIMARY KEY,
    job_execution_id BIGINT
        constraint log_job_execution_id references batch_job_execution,
    line_number      BIGINT,
    log_entry        VARCHAR(10000)
);

CREATE SEQUENCE bulk_import_log_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE bulk_import
    alter column import_file_url type VARCHAR(500);
ALTER TABLE bulk_import
    alter column log_file_url type VARCHAR(500);