create table recording_obligation
(
    sloid                varchar(500) PRIMARY KEY,
    recording_obligation boolean     NOT NULL default true,
    creation_date        TIMESTAMP   NOT NULL,
    creator              VARCHAR(50) NOT NULL,
    edition_date         TIMESTAMP   NOT NULL,
    editor               VARCHAR(50) NOT NULL,
    version              BIGINT      NOT NULL DEFAULT 0
)