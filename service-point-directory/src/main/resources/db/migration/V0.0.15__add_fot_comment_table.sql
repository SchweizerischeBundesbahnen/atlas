CREATE TABLE service_point_fot_comment
(
    service_point_number INTEGER PRIMARY KEY,
    fot_comment          VARCHAR(2000) NOT NULL,
    creation_date        TIMESTAMP     NOT NULL,
    creator              VARCHAR(50)   NOT NULL,
    edition_date         TIMESTAMP     NOT NULL,
    editor               VARCHAR(50)   NOT NULL,
    version              BIGINT        NOT NULL DEFAULT 0
);

alter table service_point_version
    drop column comment;