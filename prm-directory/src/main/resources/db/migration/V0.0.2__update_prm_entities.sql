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

ALTER TABLE relation_version
    ADD COLUMN reference_point_sloid VARCHAR(500) NOT NULL;

ALTER TABLE relation_version
    DROP CONSTRAINT relation_sloid_unique;

ALTER TABLE relation_version
    ADD CONSTRAINT relation_sloids_unique UNIQUE (sloid, reference_point_sloid, valid_from);

------------------- Remove NOT_NULL Constraints -----------------------
ALTER TABLE stop_place_version
    ALTER COLUMN alternative_transport DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN assistance_availability DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN assistance_service DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN audio_ticket_machine DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN dynamic_audio_system DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN dynamic_optic_system DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN visual_info DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN wheelchair_ticket_machine DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN assistance_request_fulfilled DROP NOT NULL;
ALTER TABLE stop_place_version
    ALTER COLUMN ticket_machine DROP NOT NULL;

------------------ Rename StopPlace to StopPoint -----

DROP SEQUENCE IF EXISTS stop_place_version_seq;

CREATE SEQUENCE stop_point_version_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE stop_place_version
    RENAME CONSTRAINT stop_place_sloid_unique TO stop_point_sloid_unique;

ALTER TABLE stop_place_version
    RENAME TO stop_point_version;

ALTER TABLE stop_place_version_means_of_transport
    RENAME TO stop_point_version_means_of_transport;

ALTER TABLE stop_point_version_means_of_transport
    RENAME COLUMN stop_place_version_id to stop_point_version_id;

