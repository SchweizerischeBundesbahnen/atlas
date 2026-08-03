-----------------------------------------------------------------------------------------
-- Transport Company
-----------------------------------------------------------------------------------------

create table shared_transport_company
(
    id                       bigint not null primary key,
    number                   varchar(50),
    abbreviation             varchar(50),
    description              varchar(200),
    business_register_name   varchar(950),
    business_register_number varchar(50)
);

-----------------------------------------------------------------------------------------
-- Timetable Hearing Statement
-----------------------------------------------------------------------------------------

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

CREATE SEQUENCE timetable_hearing_statement_seq START WITH 1000 INCREMENT BY 1;

create table timetable_hearing_statement_emails
(
    timetable_hearing_statement_id bigint       not null,
    emails                         varchar(255) not null,
    FOREIGN KEY (timetable_hearing_statement_id) REFERENCES timetable_hearing_statement (id)
);

create table timetable_hearing_statement_responsible_transport_companies
(
    timetable_hearing_statement_id bigint not null,
    transport_company_id           bigint not null,
    FOREIGN KEY (timetable_hearing_statement_id) REFERENCES timetable_hearing_statement (id),
    FOREIGN KEY (transport_company_id) REFERENCES shared_transport_company (id)
);

-----------------------------------------------------------------------------------------
-- Documents
-----------------------------------------------------------------------------------------
create table statement_document
(
    id                             bigint       not null,
    timetable_hearing_statement_id bigint       not null,
    file_name                      varchar(500) not null,
    file_size                      bigint       not null,
    anonymous                      boolean,
    FOREIGN KEY (timetable_hearing_statement_id) REFERENCES timetable_hearing_statement (id)
);

-----------------------------------------------------------------------------------------
-- Timetable Hearing Year
-----------------------------------------------------------------------------------------

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
