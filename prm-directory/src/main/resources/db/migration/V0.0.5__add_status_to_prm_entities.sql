ALTER TABLE stop_point_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED'; 

ALTER TABLE contact_point_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';

ALTER TABLE parking_lot_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';

ALTER TABLE platform_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';

ALTER TABLE toilet_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';

ALTER TABLE reference_point_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';

ALTER TABLE relation_version
    ADD COLUMN status VARCHAR(50) NOT NULL default 'VALIDATED';
