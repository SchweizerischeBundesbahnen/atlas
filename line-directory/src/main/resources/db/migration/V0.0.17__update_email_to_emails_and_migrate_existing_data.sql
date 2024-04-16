-- Step 1: Backup your data (recommended)
-- Backup your_table to ensure you have a copy of the data before making changes.

-- Step 2: Add a new column to store the list of emails
CREATE TABLE timetable_hearing_statement_emails (
                        id SERIAL PRIMARY KEY,
                        email VARCHAR(255) NOT NULL,
                        timetable_hearing_statement_id INT,
                        FOREIGN KEY (timetable_hearing_statement_id) REFERENCES timetable_hearing_statement(id)
);

-- Step 3: Migrate existing email data to the new column
INSERT INTO timetable_hearing_statement_emails (email, timetable_hearing_statement_id)
SELECT email, id FROM timetable_hearing_statement;
--

-- Step 4: Verify the migration
-- Run SELECT queries to verify that the data has been migrated correctly.

-- Step 5: (Optional) Drop the old column
-- If you're confident that the migration was successful and the old column is no longer needed, you can drop it.
-- ALTER TABLE your_table DROP COLUMN email;
