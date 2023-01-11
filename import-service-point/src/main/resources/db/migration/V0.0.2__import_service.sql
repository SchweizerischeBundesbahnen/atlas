CREATE TABLE import_process_item
(
    id                   BIGINT PRIMARY KEY,
    step_execution_id    BIGINT,
    service_point_number INTEGER NOT NULL
);

CREATE SEQUENCE import_process_item_seq START WITH 1000 INCREMENT BY 1;