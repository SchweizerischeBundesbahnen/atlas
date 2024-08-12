CREATE TABLE bulk_import
(
    id              BIGINT PRIMARY KEY,
    application     VARCHAR(50),
    object_type     VARCHAR(50),
    import_type     VARCHAR(50),
    import_file_url VARCHAR(100),
    log_file_url    VARCHAR(100),
    creator         VARCHAR(50),
    in_name_of      VARCHAR(50),
    creation_date   TIMESTAMP
);

CREATE SEQUENCE bulk_import_seq START WITH 1000 INCREMENT BY 1;