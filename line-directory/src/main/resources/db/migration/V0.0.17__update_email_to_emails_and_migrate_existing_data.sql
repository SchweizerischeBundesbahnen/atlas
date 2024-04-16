-- Step 1: Backup your data (recommended)
-- Backup your_table to ensure you have a copy of the data before making changes.

-- Step 2: Add a new column to store the list of emails
ALTER TABLE timetable_hearing_statement ADD COLUMN emails VARCHAR(1000);

-- Step 3: Migrate existing email data to the new column
UPDATE timetable_hearing_statement SET emails = ARRAY(SELECT email FROM timetable_hearing_statement);
--

ALTER TABLE timetable_hearing_statement ALTER COLUMN email DROP NOT NULL;


-- Step 4: Verify the migration
-- Run SELECT queries to verify that the data has been migrated correctly.

-- Step 5: (Optional) Drop the old column
-- If you're confident that the migration was successful and the old column is no longer needed, you can drop it.
-- ALTER TABLE your_table DROP COLUMN email;
