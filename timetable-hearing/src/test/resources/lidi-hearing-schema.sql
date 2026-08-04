create table timetable_hearing_statement
(
    id                                      bigint                not null primary key,
    timetable_year                          bigint                not null,
    statement_status                        varchar(50)           not null,
    ttfnid                                  varchar(500),
    swiss_canton                            varchar(50),
    stop_place                              varchar(255),
    first_name                              varchar(100),
    last_name                               varchar(100),
    organisation                            varchar(100),
    street                                  varchar(100),
    zip                                     bigint,
    city                                    varchar(50),
    statement                               varchar(5000)         not null,
    public_comment                          varchar(5000),
    creation_date                           timestamp             not null,
    creator                                 varchar(50)           not null,
    edition_date                            timestamp             not null,
    editor                                  varchar(50)           not null,
    version                                 bigint  default 0     not null,
    responsible_transport_companies_display varchar(250),
    canton_transfer_comment                 varchar(280),
    old_swiss_canton                        varchar(50),
    statement_anonymous                     boolean,
    anonymous_statement                     varchar(5000),
    topic                                   varchar(255),
    internal_comment                        varchar(5000),
    dossier_id                              bigint,
    dossier_contact_mail                    varchar(255),
    dossier_contact_sbbuid                  varchar(50),
    data_protection_checked                 boolean default false not null
);

create table timetable_hearing_statement_emails
(
    timetable_hearing_statement_id bigint       not null,
    emails                         varchar(255) not null
);

create table timetable_hearing_statement_responsible_transport_companies
(
    timetable_hearing_statement_id bigint not null,
    transport_company_id           bigint not null
);

create table statement_document
(
    id                             bigint       not null,
    timetable_hearing_statement_id bigint       not null,
    file_name                      varchar(500) not null,
    file_size                      bigint       not null,
    anonymous                      boolean
);

create table timetable_hearing_year
(
    timetable_year               bigint           not null primary key,
    hearing_from                 date             not null,
    hearing_to                   date             not null,
    hearing_status               varchar(50)      not null,
    statement_creatable_external boolean          not null,
    statement_creatable_internal boolean          not null,
    statement_editable           boolean          not null,
    creation_date                timestamp        not null,
    creator                      varchar(50)      not null,
    edition_date                 timestamp        not null,
    editor                       varchar(50)      not null,
    version                      bigint default 0 not null
);

-- Test data ------------------------------------------------------------------------------------------------------------

INSERT INTO timetable_hearing_year (timetable_year, hearing_from, hearing_to, hearing_status,
                                    statement_creatable_external, statement_creatable_internal, statement_editable,
                                    creation_date, creator, edition_date, editor, version)
VALUES (2024, '2023-05-01', '2023-06-01', 'ACTIVE', true, true, true,
        '2024-01-01 10:00:00', 'creator', '2024-01-01 10:00:00', 'editor', 0);

INSERT INTO timetable_hearing_statement (id, timetable_year, statement_status, swiss_canton, statement,
                                         first_name, last_name, city, zip,
                                         creation_date, creator, edition_date, editor, version, data_protection_checked)
VALUES (100, 2024, 'RECEIVED', 'BERN', 'Ich moechte bitte mehr Zuege', 'Mike', 'von Bike', 'Bern', 3000,
        '2024-01-02 10:00:00', 'creator', '2024-01-02 10:00:00', 'editor', 0, false),
       (101, 2024, 'ACCEPTED', 'ZURICH', 'Bitte spaetere Verbindungen', 'Jane', 'Doe', 'Zuerich', 8000,
        '2024-01-03 10:00:00', 'creator', '2024-01-03 10:00:00', 'editor', 0, false);

INSERT INTO timetable_hearing_statement_emails (timetable_hearing_statement_id, emails)
VALUES (100, 'mike@thebike.com'),
       (101, 'jane@doe.com');

INSERT INTO statement_document (id, timetable_hearing_statement_id, file_name, file_size, anonymous)
VALUES (200, 100, 'document-1.pdf', 6454, false),
       (201, 100, 'document-2.pdf', 2454, false);

