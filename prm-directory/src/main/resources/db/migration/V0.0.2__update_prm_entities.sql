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

--------------------- Remove NOT_NULL Constraints -----------------------
ALTER TABLE stop_place_version ALTER COLUMN alternative_transport DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN assistance_availability DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN assistance_service DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN audio_ticket_machine DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN dynamic_audio_system DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN dynamic_optic_system DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN visual_info DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN wheelchair_ticket_machine DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN assistance_request_fulfilled DROP NOT NULL;
ALTER TABLE stop_place_version ALTER COLUMN ticket_machine DROP NOT NULL;
