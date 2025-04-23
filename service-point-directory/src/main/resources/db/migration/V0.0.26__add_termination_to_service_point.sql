alter table service_point_version
    add column termination_in_progress BOOLEAN NOT NULL DEFAULT FALSE;
