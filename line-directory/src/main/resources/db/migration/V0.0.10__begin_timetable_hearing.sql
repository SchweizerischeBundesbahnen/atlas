-----------------------------------------------------------------------------------------
-- Timetable Hearing Year
-----------------------------------------------------------------------------------------

CREATE TABLE timetable_hearing_year
(
    timetable_year               BIGINT      NOT NULL PRIMARY KEY,
    hearing_from                 DATE        NOT NULL,
    hearing_to                   DATE        NOT NULL,
    hearing_status               VARCHAR(50) NOT NULL,
    statement_creatable_external BOOLEAN     NOT NULL,
    statement_creatable_internal BOOLEAN     NOT NULL,
    statement_editable           BOOLEAN     NOT NULL,
    creation_date                TIMESTAMP   NOT NULL,
    creator                      VARCHAR(50) NOT NULL,
    edition_date                 TIMESTAMP   NOT NULL,
    editor                       VARCHAR(50) NOT NULL,
    version                      BIGINT      NOT NULL DEFAULT 0
);

-----------------------------------------------------------------------------------------
-- Timetable Hearing Statement
-----------------------------------------------------------------------------------------

CREATE TABLE timetable_hearing_statement
(
    id               BIGINT        NOT NULL PRIMARY KEY,
    timetable_year   BIGINT        NOT NULL,
    statement_status VARCHAR(50)   NOT NULL,
    ttfnid           VARCHAR(500)  NULL,
    swiss_canton     VARCHAR(50)   NULL,
    stop_place       VARCHAR(50)   NULL,
    first_name       VARCHAR(100)  NULL,
    last_name        VARCHAR(100)  NULL,
    organisation     VARCHAR(100)  NULL,
    street           VARCHAR(100)  NULL,
    zip              BIGINT        NULL,
    city             VARCHAR(50)   NULL,
    email            VARCHAR(100)  NOT NULL,
    statement        VARCHAR(5000) NOT NULL,
    justification    VARCHAR(5000) NULL,
    creation_date    TIMESTAMP     NOT NULL,
    creator          VARCHAR(50)   NOT NULL,
    edition_date     TIMESTAMP     NOT NULL,
    editor           VARCHAR(50)   NOT NULL,
    version          BIGINT        NOT NULL DEFAULT 0
);

CREATE SEQUENCE timetable_hearing_statement_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Timetable Hearing Statement Responsible Transport Companies
-----------------------------------------------------------------------------------------

CREATE TABLE timetable_hearing_statement_responsible_transport_companies
(
    id                             BIGINT NOT NULL PRIMARY KEY,
    timetable_hearing_statement_id BIGINT NOT NULL,
    transport_company_id           BIGINT NOT NULL,
    number                         varchar(50),
    abbreviation                   varchar(50),
    business_register_name         varchar(950),
    foreign key (timetable_hearing_statement_id) references timetable_hearing_statement (id)
);

CREATE SEQUENCE timetable_hearing_statement_responsible_transport_companies_seq START WITH 1000 INCREMENT BY 1;

-----------------------------------------------------------------------------------------
-- Timetable Hearing Statement Documents
-----------------------------------------------------------------------------------------

CREATE TABLE statement_document
(
    id                             BIGINT       NOT NULL,
    timetable_hearing_statement_id BIGINT       NOT NULL,
    file_name                      VARCHAR(500) NOT NULL,
    file_size                      BIGINT       NOT NULL,
    foreign key (timetable_hearing_statement_id) references timetable_hearing_statement (id)
);

CREATE SEQUENCE statement_document_seq START WITH 1000 INCREMENT BY 1;