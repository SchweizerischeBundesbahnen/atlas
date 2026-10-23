-----------------------------------------------------------------------------------------
-- Linienmodell 3.0 - BAV comment (ATLAS-3606)
-- Separate comment field for the BAV (FOT), distinct from the TU comment column "comment".
-- Additive and nullable: no backfill, existing rows keep NULL.
-----------------------------------------------------------------------------------------

ALTER TABLE line_version_v3 ADD COLUMN fot_comment VARCHAR(1500) NULL;
