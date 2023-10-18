ALTER TABLE stop_place_version
    RENAME COLUMN alternative_condition to assistance_condition;

ALTER TABLE stop_place_version
    RENAME COLUMN additional_info to additional_information;

ALTER TABLE information_desk_version
    RENAME COLUMN info to additional_information;

ALTER TABLE parking_lot_version
    RENAME COLUMN info to additional_information;

ALTER TABLE platform_version
    RENAME COLUMN additional_info to additional_information;

ALTER TABLE ticket_counter_version
    RENAME COLUMN info to additional_information;

ALTER TABLE toilet_version
    RENAME COLUMN info to additional_information;

ALTER TABLE reference_point_version
    ADD COLUMN additional_information VARCHAR(2000);

ALTER TABLE stop_place_version
    ALTER COLUMN city TYPE VARCHAR(75);

ALTER TABLE parking_lot_version
    ALTER COLUMN prm_places_available TYPE VARCHAR(50);