alter table traffic_point_element_version
alter column sloid type varchar(128);

alter table traffic_point_element_version
alter column designation type varchar(40);

alter table traffic_point_element_version
alter column designation_operational type varchar(20);

alter table traffic_point_element_version
alter column length type numeric(13, 3);

alter table traffic_point_element_version_geolocation
alter column height type numeric(10, 3);