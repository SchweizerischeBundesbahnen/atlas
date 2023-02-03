alter table service_point_version
    add column operating_point_technical_timetable_type VARCHAR(50);
alter table service_point_version
    add column operating_point_traffic_point_type VARCHAR(50);
alter table service_point_version
    add column operating_point_without_timetable_type VARCHAR(50);
