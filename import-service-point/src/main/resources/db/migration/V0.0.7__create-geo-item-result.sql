drop table import_process_item;
drop sequence import_process_item_seq;

CREATE TABLE geo_update_import_process_item
(
    id                 BIGINT PRIMARY KEY,
    step_execution_id  BIGINT,
    job_execution_name VARCHAR(100) NOT NULL,
    sloid              VARCHAR(50) NOT NULL,
    service_point_id   BIGINT NOT NULL,
    response_status    VARCHAR(50) NOT NULL,
    response_message   VARCHAR(2500) NOT NULL
);

CREATE SEQUENCE geo_update_import_process_item_seq START WITH 1000 INCREMENT BY 1;
