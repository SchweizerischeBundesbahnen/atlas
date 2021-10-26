CREATE SEQUENCE slnid_seq START WITH 1000000 INCREMENT BY 1;

-- SLNID has to be unique
ALTER TABLE line_version
    ADD CONSTRAINT line_slnid_unique UNIQUE (slnid);
ALTER TABLE subline_version
    ADD CONSTRAINT subline_slnid_unique UNIQUE (slnid);

ALTER TABLE line_version
    ALTER COLUMN description TYPE VARCHAR(255);

ALTER TABLE subline_version
    ALTER COLUMN description TYPE VARCHAR(255);


-- Mandatory for Line
ALTER TABLE line_version
    ALTER COLUMN swiss_line_number set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN slnid set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN status set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN type set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN payment_type set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN valid_from set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN valid_to set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN business_organisation set NOT NULL;

ALTER TABLE line_version
    ALTER COLUMN creation_date set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN edition_date set NOT NULL;

-- Mandatory for Subline
ALTER TABLE subline_version
    ALTER COLUMN swiss_subline_number set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN status set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN slnid set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN type set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN payment_type set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN valid_from set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN valid_to set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN business_organisation set NOT NULL;

ALTER TABLE subline_version
    ALTER COLUMN creation_date set NOT NULL;
ALTER TABLE subline_version
    ALTER COLUMN edition_date set NOT NULL;