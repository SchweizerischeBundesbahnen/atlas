ALTER TABLE company
    ALTER COLUMN uic_code TYPE VARCHAR(10);

ALTER TABLE company
    DROP COLUMN name_ascii;
ALTER TABLE company
    DROP COLUMN passenger_flag;
ALTER TABLE company
    DROP COLUMN freight_flag;
ALTER TABLE company
    DROP COLUMN infrastructure_flag;
ALTER TABLE company
    DROP COLUMN other_company_flag;
ALTER TABLE company
    DROP COLUMN ne_entity_flag;
ALTER TABLE company
    DROP COLUMN ce_entity_flag;
ALTER TABLE company
    DROP COLUMN add_date;
ALTER TABLE company
    DROP COLUMN modified_date;
ALTER TABLE company
    DROP COLUMN creation_date;
ALTER TABLE company
    DROP COLUMN edition_date;