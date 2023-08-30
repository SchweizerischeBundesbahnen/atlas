create sequence service_point_version_geolocation_seq;

create sequence service_point_version_seq;

create sequence loading_point_version_seq;

create sequence traffic_point_element_version_geolocation_seq;

create sequence traffic_point_element_version_seq;

create table service_point_version_geolocation
(
    id                        bigint           not null
        primary key,
    spatial_reference         varchar(50)      not null,
    east                      numeric(19, 11),
    north                     numeric(19, 11),
    height                    numeric(6, 2),
    country                   varchar(50),
    swiss_canton              varchar(50),
    swiss_district_name       varchar(255),
    swiss_district_number     smallint,
    swiss_municipality_number smallint,
    swiss_municipality_name   varchar(255),
    swiss_locality_name       varchar(255),
    creation_date             timestamp        not null,
    creator                   varchar(50)      not null,
    edition_date              timestamp        not null,
    editor                    varchar(50)      not null,
    version                   bigint default 0 not null
);

create table service_point_version
(
    id                                       bigint                not null
        primary key,
    service_point_geolocation_id             bigint
        constraint fk_service_point_geolocation_id
            references service_point_version_geolocation,
    number                                   integer               not null,
    sloid                                    varchar(500),
    number_short                             integer               not null,
    country                                  varchar(50)           not null,
    designation_long                         varchar(50),
    designation_official                     varchar(30)           not null,
    abbreviation                             varchar(6),
    status_didok3                            varchar(50)           not null,
    sort_code_of_destination_station         varchar(10),
    business_organisation                    varchar(50)           not null,
    operating_point_type                     varchar(50),
    stop_point_type                          varchar(50),
    status                                   varchar(50)           not null,
    operating_point_kilometer_master         integer,
    operating_point_route_network            boolean default false not null,
    valid_from                               date                  not null,
    valid_to                                 date                  not null,
    creation_date                            timestamp             not null,
    creator                                  varchar(50)           not null,
    edition_date                             timestamp             not null,
    editor                                   varchar(50)           not null,
    version                                  bigint  default 0     not null,
    freight_service_point                    boolean               not null,
    operating_point                          boolean               not null,
    operating_point_with_timetable           boolean               not null,
    operating_point_technical_timetable_type varchar(50),
    operating_point_traffic_point_type       varchar(50),
    constraint spv_number_unique
        unique (number, valid_from)
);

CREATE TABLE service_point_fot_comment
(
    service_point_number INTEGER PRIMARY KEY,
    fot_comment          VARCHAR(2000) NOT NULL,
    creation_date        TIMESTAMP     NOT NULL,
    creator              VARCHAR(50)   NOT NULL,
    edition_date         TIMESTAMP     NOT NULL,
    editor               VARCHAR(50)   NOT NULL,
    version              BIGINT        NOT NULL DEFAULT 0
);

create table service_point_version_categories
(
    service_point_version_id bigint      not null,
    categories               varchar(50) not null
);

create table loading_point_version
(
    id                   bigint           not null
        primary key,
    number               bigint           not null,
    designation          varchar(12)      not null,
    designation_long     varchar(35),
    connection_point     boolean          not null,
    service_point_number integer          not null,
    valid_from           date             not null,
    valid_to             date             not null,
    creation_date        timestamp        not null,
    creator              varchar(50)      not null,
    edition_date         timestamp        not null,
    editor               varchar(50)      not null,
    version              bigint default 0 not null
);

create table service_point_version_means_of_transport
(
    service_point_version_id bigint      not null,
    means_of_transport       varchar(50) not null
);

create table traffic_point_element_version_geolocation
(
    id                bigint           not null
        primary key,
    spatial_reference varchar(50)      not null,
    east              numeric(19, 11),
    north             numeric(19, 11),
    height            numeric(6, 2),
    creation_date     timestamp        not null,
    creator           varchar(50)      not null,
    edition_date      timestamp        not null,
    editor            varchar(50)      not null,
    version           bigint default 0 not null
);

create table traffic_point_element_version
(
    id                           bigint           not null
        primary key,
    sloid                        varchar(500)     not null,
    parent_sloid                 varchar(500),
    designation                  varchar(50),
    designation_operational      varchar(50),
    traffic_point_element_type   varchar(50),
    length                       numeric(13, 2),
    boarding_area_height         numeric(5, 2),
    compass_direction            numeric(5, 2),
    service_point_number         bigint           not null,
    valid_from                   date             not null,
    valid_to                     date             not null,
    traffic_point_geolocation_id bigint
        constraint fk_traffic_point_element_version_geolocation_id
            references traffic_point_element_version_geolocation,
    creation_date                timestamp        not null,
    creator                      varchar(50)      not null,
    edition_date                 timestamp        not null,
    editor                       varchar(50)      not null,
    version                      bigint default 0 not null
);

create table shared_business_organisation_version
(
    id                  bigint primary key,
    sboid               varchar(32) not null,
    abbreviation_de     varchar(10) not null,
    abbreviation_fr     varchar(10) not null,
    abbreviation_it     varchar(10) not null,
    abbreviation_en     varchar(10) not null,
    description_de      varchar(60) not null,
    description_fr      varchar(60) not null,
    description_it      varchar(60) not null,
    description_en      varchar(60) not null,
    organisation_number integer     not null,
    status              varchar(50) not null,
    valid_from          date        not null,
    valid_to            date        not null
);
