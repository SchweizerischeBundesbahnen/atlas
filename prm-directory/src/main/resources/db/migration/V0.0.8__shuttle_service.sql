ALTER TABLE stop_point_version ADD COLUMN shuttle_service VARCHAR(50);

update stop_point_version set shuttle_service = alternative_transport where alternative_transport is not null;
update stop_point_version set alternative_transport = 'NO' where alternative_transport is not null;