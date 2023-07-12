alter table loading_point_version_geolocation
    alter column height type numeric(10, 5);

alter table service_point_version_geolocation
    alter column height type numeric(10, 5);

alter table traffic_point_element_version_geolocation
    alter column height type numeric(10, 5);
