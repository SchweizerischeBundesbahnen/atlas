-- Step 1: Backup your data (recommended)
-- Backup your_table to ensure you have a copy of the data before making changes.

-- -- Step 2: Add a new column to store the list of emails
-- ALTER TABLE timetable_hearing_statement ADD COLUMN emails VARCHAR(1000) NOT NULL;
--
-- -- Step 3: Migrate existing email data to the new column
-- UPDATE timetable_hearing_statement SET emails = email;
-- --
--
-- ALTER TABLE timetable_hearing_statement ALTER COLUMN email DROP NOT NULL;


-- Step 4: Verify the migration
-- Run SELECT queries to verify that the data has been migrated correctly.

-- Step 5: (Optional) Drop the old column
-- If you're confident that the migration was successful and the old column is no longer needed, you can drop it.
-- ALTER TABLE your_table DROP COLUMN email;



-- -- Step 1: Add the new column 'emails' to the table
-- ALTER TABLE timetable_hearing_statement ADD COLUMN emails VARCHAR(1000) NOT NULL DEFAULT '';
--
-- -- Step 2: Update the new 'emails' column with data from the existing 'email' column
-- UPDATE timetable_hearing_statement SET emails = email;
--
-- -- Step 3: Remove the 'NOT NULL' constraint from the 'email' column
-- ALTER TABLE timetable_hearing_statement ALTER COLUMN email DROP NOT NULL;
--
-- -- Step 4: Drop the 'email' column
-- ALTER TABLE timetable_hearing_statement DROP COLUMN email;


CREATE TABLE timetable_hearing_statement_emails (
    timetable_hearing_statement_id BIGINT       NOT NULL,
    email                          VARCHAR(255) NOT NULL
);

INSERT INTO timetable_hearing_statement_emails (email, timetable_hearing_statement_id)
SELECT email, id FROM timetable_hearing_statement;

ALTER TABLE timetable_hearing_statement ALTER COLUMN email DROP NOT NULL;