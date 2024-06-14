ALTER TABLE stop_point_workflow
    ADD applicant_mail VARCHAR(255);

ALTER TABLE stop_point_workflow
    ALTER COLUMN start_date DROP NOT NULL;