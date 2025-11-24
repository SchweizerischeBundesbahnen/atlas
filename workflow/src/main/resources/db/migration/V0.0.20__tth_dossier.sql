CREATE SEQUENCE tth_dossier_seq START WITH 1000 INCREMENT BY 1;

create table tth_dossier
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    topic                 VARCHAR(500) not null,
    dossier_status        VARCHAR(50)  not null,
    internal_comment      VARCHAR(5000),
    public_comment        VARCHAR(5000),
    bo_contact_mail       VARCHAR(255) NOT NULL,
    bo_deadline_to_answer DATE         NOT NULL,
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50)  NOT NULL,
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50)  NOT NULL
);

CREATE TABLE tth_dossier_statement_ids
(
    tth_dossier_id BIGINT NOT NULL,
    statement_ids  BIGINT NOT NULL,

    CONSTRAINT fk_tth_dossier_statement_ids_tth_dossier_id
        FOREIGN KEY (tth_dossier_id)
            REFERENCES tth_dossier (id)
);

CREATE SEQUENCE tth_dossier_question_seq START WITH 1000 INCREMENT BY 1;
create table tth_dossier_question
(
    id               BIGINT      NOT NULL PRIMARY KEY,
    tth_dossier_id   BIGINT      NOT NULL,
    question         VARCHAR(5000),
    answer_to_canton VARCHAR(5000),
    creation_date    TIMESTAMP   NOT NULL,
    creator          VARCHAR(50) NOT NULL,
    edition_date     TIMESTAMP   NOT NULL,
    editor           VARCHAR(50) NOT NULL,

    CONSTRAINT fk_tth_dossier_question_tth_dossier_id
        FOREIGN KEY (tth_dossier_id)
            REFERENCES tth_dossier (id)
);