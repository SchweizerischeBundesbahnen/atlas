drop sequence loading_point_version_geolocation_seq;

alter table loading_point_version
    drop constraint fk_loading_point_version_geolocation_id;

alter table loading_point_version
    drop column loading_point_geolocation_id;

drop table loading_point_version_geolocation;

alter table loading_point_version
    add constraint uc_version unique (service_point_number, number, valid_from);
