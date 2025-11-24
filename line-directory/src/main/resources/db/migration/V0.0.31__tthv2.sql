alter table timetable_hearing_statement
    rename column justification to public_comment;
alter table timetable_hearing_statement
    rename column comment to canton_transfer_comment;

alter table timetable_hearing_statement
    add column statement_anonymous boolean default false not null;
alter table timetable_hearing_statement
    add column topic varchar(255);
alter table timetable_hearing_statement
    add column internal_comment varchar(5000);

alter table timetable_hearing_statement
    add column dossier_id BIGINT;
alter table timetable_hearing_statement
    add column dossier_contact_mail varchar(255);

alter table statement_document
    add column anonymous boolean default false not null;