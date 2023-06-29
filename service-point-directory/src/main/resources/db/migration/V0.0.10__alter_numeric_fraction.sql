alter table traffic_point_element_version
    alter column length type numeric(13, 3);

alter table loading_point_version_geolocation
    alter column height type numeric(10, 3);

alter table service_point_version_geolocation
    alter column height type numeric(10, 3);

alter table traffic_point_element_version_geolocation
    alter column height type numeric(10, 3);
