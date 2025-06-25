ALTER TABLE termination_stop_point_workflow
    ALTER COLUMN bo_termination_date DROP NOT NULL;
ALTER TABLE termination_stop_point_workflow
    ALTER COLUMN info_plus_termination_date DROP NOT NULL;;
ALTER TABLE termination_stop_point_workflow
    ALTER COLUMN nova_termination_date DROP NOT NULL;;
