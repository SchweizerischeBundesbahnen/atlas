create table tth_dossier
(
    id                    bigint       not null primary key,
    topic                 varchar(500) not null,
    dossier_status        varchar(50)  not null,
    internal_comment      varchar(5000),
    public_comment        varchar(5000),
    bo_contact_mail       varchar(255),
    bo_deadline_to_answer date,
    creation_date         timestamp    not null,
    creator               varchar(50)  not null,
    edition_date          timestamp    not null,
    editor                varchar(50)  not null,
    swiss_canton          varchar(50),
    timetable_year        bigint,
    bo_contact_sbbuid     varchar(50)
);

create table tth_dossier_statement_ids
(
    tth_dossier_id bigint not null,
    statement_ids  bigint not null
);

create table tth_dossier_question
(
    id               bigint      not null primary key,
    tth_dossier_id   bigint      not null,
    question         varchar(5000),
    answer_to_canton varchar(5000),
    creation_date    timestamp   not null,
    creator          varchar(50) not null,
    edition_date     timestamp   not null,
    editor           varchar(50) not null
);

-- Test data ------------------------------------------------------------------------------------------------------------

INSERT INTO tth_dossier (id, topic, dossier_status, internal_comment, public_comment, bo_contact_mail,
                         bo_deadline_to_answer, creation_date, creator, edition_date, editor, swiss_canton,
                         timetable_year, bo_contact_sbbuid)
VALUES (300, 'Bern, Salem - Takt', 'ADDED', 'internal', 'public', 'bern@mobil.be', '2024-02-01',
        '2024-01-01 10:00:00', 'creator', '2024-01-01 10:00:00', 'editor', 'BERN', 2024, 'u123456');

INSERT INTO tth_dossier_statement_ids (tth_dossier_id, statement_ids)
VALUES (300, 100),
       (300, 101);

INSERT INTO tth_dossier_question (id, tth_dossier_id, question, answer_to_canton, creation_date, creator, edition_date,
                                  editor)
VALUES (400, 300, 'Kann der Takt erhoeht werden?', null, '2024-01-01 10:00:00', 'creator', '2024-01-01 10:00:00',
        'editor');

