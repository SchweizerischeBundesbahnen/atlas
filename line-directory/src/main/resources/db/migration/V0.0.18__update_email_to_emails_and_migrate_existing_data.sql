CREATE TABLE timetable_hearing_statement_emails (
    timetable_hearing_statement_id  BIGINT       NOT NULL,
    emails                          VARCHAR(255) NOT NULL,
    FOREIGN KEY (timetable_hearing_statement_id) REFERENCES timetable_hearing_statement(id)
);

INSERT INTO timetable_hearing_statement_emails (emails, timetable_hearing_statement_id)
SELECT email, id FROM timetable_hearing_statement;

ALTER TABLE timetable_hearing_statement ALTER COLUMN email DROP NOT NULL;

ALTER TABLE timetable_hearing_statement DROP COLUMN email;