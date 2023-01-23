alter table service_point_version
    add column freight_service_point BOOLEAN NOT NULL;
alter table service_point_version
    add column operating_point BOOLEAN NOT NULL;
alter table service_point_version
    add column operating_point_with_timetable BOOLEAN NOT NULL;