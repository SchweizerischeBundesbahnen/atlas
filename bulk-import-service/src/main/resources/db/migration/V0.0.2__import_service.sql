CREATE TABLE import_process_item
(
    id                 BIGINT PRIMARY KEY,
    step_execution_id  BIGINT,
    job_execution_name VARCHAR(100),
    item_number        INTEGER NOT NULL,
    response_status    VARCHAR(50),
    response_message   VARCHAR(2500)
);

CREATE SEQUENCE import_process_item_seq START WITH 1000 INCREMENT BY 1;