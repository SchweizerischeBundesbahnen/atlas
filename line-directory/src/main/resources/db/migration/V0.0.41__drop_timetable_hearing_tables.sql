-- The timetable hearing statement domain has been extracted into the dedicated
-- timetable-hearing service. Data has been migrated, so the old tables and
-- sequences can be dropped from line-directory.

DROP TABLE IF EXISTS statement_document;
DROP TABLE IF EXISTS timetable_hearing_statement_emails;
DROP TABLE IF EXISTS timetable_hearing_statement_responsible_transport_companies;
DROP TABLE IF EXISTS timetable_hearing_statement;
DROP TABLE IF EXISTS timetable_hearing_year;
DROP TABLE IF EXISTS shared_transport_company;

DROP SEQUENCE IF EXISTS statement_document_seq;
DROP SEQUENCE IF EXISTS timetable_hearing_statement_seq;
DROP SEQUENCE IF EXISTS timetable_hearing_statement_responsible_transport_companies_seq;
