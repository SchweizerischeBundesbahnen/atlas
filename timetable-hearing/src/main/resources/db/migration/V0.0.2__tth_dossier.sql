-----------------------------------------------------------------------------------------
-- Dossier
-----------------------------------------------------------------------------------------

CREATE SEQUENCE dossier_seq START WITH 1000 INCREMENT BY 1;

create table dossier
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    topic                 VARCHAR(500) NOT NULL,
    dossier_status        VARCHAR(50)  NOT NULL,
    internal_comment      VARCHAR(5000),
    public_comment        VARCHAR(5000),
    bo_contact_mail       VARCHAR(255),
    bo_deadline_to_answer DATE,
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50)  NOT NULL,
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50)  NOT NULL,
    version               BIGINT       DEFAULT 0 NOT NULL,
    swiss_canton          VARCHAR(50),
    timetable_year        BIGINT
        CONSTRAINT fk_dossier_timetable_year
            REFERENCES timetable_hearing_year (timetable_year),
    bo_contact_sbbuid     VARCHAR(50)
);

CREATE INDEX idx_dossier_timetable_year ON dossier (timetable_year);

CREATE TABLE dossier_statement_ids
(
    dossier_id BIGINT NOT NULL,
    statement_ids  BIGINT NOT NULL,

    CONSTRAINT fk_dossier_statement_ids_dossier_id
        FOREIGN KEY (dossier_id)
            REFERENCES dossier (id),
    CONSTRAINT fk_dossier_statement_ids_statement_id
        FOREIGN KEY (statement_ids)
            REFERENCES timetable_hearing_statement (id)
);

CREATE INDEX idx_dossier_statement_ids_dossier_id ON dossier_statement_ids (dossier_id);
CREATE INDEX idx_dossier_statement_ids_statement_ids ON dossier_statement_ids (statement_ids);

CREATE SEQUENCE dossier_question_seq START WITH 1000 INCREMENT BY 1;
create table dossier_question
(
    id               BIGINT      NOT NULL PRIMARY KEY,
    dossier_id   BIGINT      NOT NULL,
    question         VARCHAR(5000),
    answer_to_canton VARCHAR(5000),
    creation_date    TIMESTAMP   NOT NULL,
    creator          VARCHAR(50) NOT NULL,
    edition_date     TIMESTAMP   NOT NULL,
    editor           VARCHAR(50) NOT NULL,
    version          BIGINT      DEFAULT 0 NOT NULL,

    CONSTRAINT fk_dossier_question_dossier_id
        FOREIGN KEY (dossier_id)
            REFERENCES dossier (id)
);

CREATE INDEX idx_dossier_question_dossier_id ON dossier_question (dossier_id);

