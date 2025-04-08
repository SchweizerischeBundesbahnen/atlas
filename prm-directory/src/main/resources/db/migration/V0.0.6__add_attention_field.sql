ALTER TABLE platform_version ADD COLUMN attention_field VARCHAR(50);

update platform_version set attention_field = 'TO_BE_COMPLETED' where
-- is reduced
parent_service_point_sloid in (
    select distinct(sloid) from stop_point_version
        join stop_point_version_means_of_transport mot
        on stop_point_version.id = mot.stop_point_version_id
    where mot.means_of_transport in ('ELEVATOR', 'BUS', 'CHAIRLIFT', 'CABLE_CAR', 'CABLE_RAILWAY', 'BOAT', 'TRAM')
);