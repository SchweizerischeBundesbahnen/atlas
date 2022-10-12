ALTER TABLE user_permission
    ADD creation_date TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE user_permission
    ADD creator VARCHAR(50) NOT NULL DEFAULT 'Atlas';
ALTER TABLE user_permission
    ADD edition_date TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE user_permission
    ADD editor VARCHAR(50) NOT NULL DEFAULT 'Atlas';
