alter table service_point_version_means_of_transport add constraint fk_means_of_transport_service_point
    foreign key (service_point_version_id) references service_point_version;

alter table service_point_version_categories add constraint fk_categories_service_point
    foreign key (service_point_version_id) references service_point_version;
