CREATE SEQUENCE service_point_global_id_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE service_point_global_id
(
    id                   BIGINT       NOT NULL,
    service_point_number INTEGER      NOT NULL,
    global_id            VARCHAR(255) NOT NULL,
    CONSTRAINT pk_service_point_global_id PRIMARY KEY (id),
    CONSTRAINT uq_service_point_global_id_service_point_number UNIQUE (service_point_number),
    CONSTRAINT uq_service_point_global_id_global_id            UNIQUE (global_id)
);
