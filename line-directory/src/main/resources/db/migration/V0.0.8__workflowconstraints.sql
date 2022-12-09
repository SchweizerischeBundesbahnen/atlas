-- Make sure workflow id is only in one state
ALTER TABLE line_version_workflow
    ADD CONSTRAINT workflow_id_unique UNIQUE (workflow_id);