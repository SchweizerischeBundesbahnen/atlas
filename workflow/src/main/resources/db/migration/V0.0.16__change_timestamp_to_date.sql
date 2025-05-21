ALTER TABLE termination_stop_point_workflow
    RENAME COLUMN bo_termination_date to old_bo_termination_date;
ALTER TABLE termination_stop_point_workflow
    RENAME COLUMN info_plus_termination_date to old_info_plus_termination_date;
ALTER TABLE termination_stop_point_workflow
    RENAME COLUMN nova_termination_date to old_nova_termination_date;

ALTER TABLE termination_stop_point_workflow
    add column bo_termination_date DATE NOT NULL default now();
ALTER TABLE termination_stop_point_workflow
    add column info_plus_termination_date DATE NOT NULL default now();
ALTER TABLE termination_stop_point_workflow
    add column nova_termination_date DATE NOT NULL default now();

UPDATE termination_stop_point_workflow
SET bo_termination_date = (select cast(old_bo_termination_date as DATE));
UPDATE termination_stop_point_workflow
SET info_plus_termination_date = (select cast(old_info_plus_termination_date as DATE));
UPDATE termination_stop_point_workflow
SET nova_termination_date = (select cast(old_nova_termination_date as DATE));

ALTER TABLE termination_stop_point_workflow
    DROP COLUMN old_bo_termination_date;
ALTER TABLE termination_stop_point_workflow
    DROP COLUMN old_info_plus_termination_date;
ALTER TABLE termination_stop_point_workflow
    DROP COLUMN old_nova_termination_date;