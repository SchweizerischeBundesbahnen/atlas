CREATE INDEX service_point_version_geolocation_spatial_reference_index
    ON service_point_version_geolocation (spatial_reference);
CREATE INDEX service_point_version_geolocation_east_north_index
    ON service_point_version_geolocation (east, north);

CREATE INDEX service_point_version_valid_from_to_index
    ON service_point_version (valid_from, valid_to);
