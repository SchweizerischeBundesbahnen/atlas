CREATE UNIQUE INDEX only_one_workflow_in_progress ON line_version_workflow (line_version_id, workflow_processing_status)
    WHERE workflow_processing_status='IN_PROGRESS';