alter table timetable_hearing_statement add column data_protection_checked boolean default false not null;

alter table timetable_hearing_statement alter column statement_anonymous drop not null;
alter table timetable_hearing_statement alter column statement_anonymous drop default;

alter table statement_document alter column anonymous drop not null;
alter table statement_document alter column anonymous drop default;