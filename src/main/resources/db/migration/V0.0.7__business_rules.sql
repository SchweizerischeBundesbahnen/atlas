CREATE SEQUENCE ttfnid_seq START WITH 1000000 INCREMENT BY 1;

DELETE from timetable_field_number_version where number IS NULL;
ALTER TABLE timetable_field_number_version
    alter column number SET not null;

DELETE from timetable_field_number_version where status IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN status set NOT NULL;

DELETE from timetable_field_number_version where creation_date IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN creation_date set NOT NULL;

DELETE from timetable_field_number_version where edition_date IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN edition_date set NOT NULL;

DELETE from timetable_field_number_version where creator IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN creator set NOT NULL;

DELETE from timetable_field_number_version where editor IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN editor set NOT NULL;

DELETE from timetable_field_number_version where swiss_timetable_field_number IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN swiss_timetable_field_number set NOT NULL;

DELETE from timetable_field_number_version where ttfnid IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN ttfnid set NOT NULL;
