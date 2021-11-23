CREATE SEQUENCE ttfnid_seq START WITH 1000000 INCREMENT BY 1;

-- hope it has not already null values
ALTER TABLE timetable_field_number_version
    alter column number SET not null;

ALTER TABLE timetable_field_number_version
    ALTER COLUMN status set NOT NULL;

ALTER TABLE timetable_field_number_version
    ALTER COLUMN creation_date set NOT NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN edition_date set NOT NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN creator set NOT NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN editor set NOT NULL;

ALTER TABLE timetable_field_number_version
    ALTER COLUMN swiss_timetable_field_number set NOT NULL;

ALTER TABLE timetable_field_number_version
    ALTER COLUMN ttfnid set NOT NULL;
