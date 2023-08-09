drop sequence loading_point_version_geolocation_seq;

alter table loading_point_version
    drop constraint fk_loading_point_version_geolocation_id;
drop table loading_point_version_geolocation;
