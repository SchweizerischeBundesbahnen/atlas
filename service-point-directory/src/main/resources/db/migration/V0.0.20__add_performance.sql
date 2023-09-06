CREATE INDEX service_point_version_service_point_geolocation_id
    ON service_point_version (service_point_geolocation_id);
CREATE INDEX means_of_transport_service_point_version_id
    ON service_point_version_means_of_transport (service_point_version_id);
CREATE INDEX categories_service_point_version_id
    ON service_point_version_categories (service_point_version_id);