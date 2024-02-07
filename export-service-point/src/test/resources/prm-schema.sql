create sequence reference_point_version_seq start with 1000;

create sequence platform_version_seq start with 1000;

create sequence toilet_version_seq start with 1000;

create sequence contact_point_version_seq start with 1000;

create sequence parking_lot_version_seq start with 1000;

create sequence relation_version_seq start with 1000;

create sequence stop_point_version_seq start with 1000;

create table shared_business_organisation_version
(
    id                  bigint      not null primary key,
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

create table stop_point_version
(
    id                              bigint           not null
        constraint stop_place_version_pkey
            primary key,
    sloid                           varchar(500)     not null,
    number                          integer          not null,
    free_text                       varchar(2000),
    address                         varchar(2000),
    zip_code                        varchar(50),
    city                            varchar(75),
    alternative_transport           varchar(50),
    alternative_transport_condition varchar(2000),
    assistance_availability         varchar(50),
    assistance_condition            varchar(2000),
    assistance_service              varchar(50),
    audio_ticket_machine            varchar(50),
    additional_information          varchar(2000),
    dynamic_audio_system            varchar(50),
    dynamic_optic_system            varchar(50),
    info_ticket_machine             varchar(2000),
    interoperable                   boolean,
    url                             varchar(500),
    visual_info                     varchar(50),
    wheelchair_ticket_machine       varchar(50),
    assistance_request_fulfilled    varchar(50),
    ticket_machine                  varchar(50),
    valid_from                      date             not null,
    valid_to                        date             not null,
    creation_date                   timestamp        not null,
    creator                         varchar(50)      not null,
    edition_date                    timestamp        not null,
    editor                          varchar(50)      not null,
    version                         bigint default 0 not null,
    constraint stop_point_sloid_unique
        unique (sloid, valid_from)
);

create table stop_point_version_means_of_transport
(
    stop_point_version_id bigint      not null,
    means_of_transport    varchar(50) not null
);

create table reference_point_version
(
    id                         bigint           not null primary key,
    sloid                      varchar(500)     not null,
    parent_service_point_sloid varchar(500)     not null,
    number                     integer          not null,
    designation                varchar(50)      not null,
    main_reference_point       boolean          not null,
    reference_point_type       varchar(50)      not null,
    valid_from                 date             not null,
    valid_to                   date             not null,
    creation_date              timestamp        not null,
    creator                    varchar(50)      not null,
    edition_date               timestamp        not null,
    editor                     varchar(50)      not null,
    version                    bigint default 0 not null,
    additional_information     varchar(2000),
    constraint reference_point_sloid_unique
        unique (sloid, valid_from)
);

create table platform_version
(
    id                         bigint           not null primary key,
    sloid                      varchar(500)     not null,
    number                     integer          not null,
    parent_service_point_sloid varchar(500)     not null,
    boarding_device            varchar(50),
    additional_information     varchar(2000),
    advice_access_info         varchar(2000),
    contrasting_areas          varchar(50),
    dynamic_audio              varchar(50),
    dynamic_visual             varchar(50),
    height                     numeric(10, 3),
    inclination                numeric(10, 3),
    inclination_longitudinal   numeric(10, 3),
    inclination_width          numeric(10, 3),
    level_access_wheelchair    varchar(50),
    partial_elevation          varchar(50),
    superelevation             numeric(10, 3),
    tactile_system             varchar(50),
    vehicle_access             varchar(50),
    wheelchair_area_length     numeric(10, 3),
    wheelchair_area_width      numeric(10, 3),
    valid_from                 date             not null,
    valid_to                   date             not null,
    creation_date              timestamp        not null,
    creator                    varchar(50)      not null,
    edition_date               timestamp        not null,
    editor                     varchar(50)      not null,
    version                    bigint default 0 not null,
    constraint platform_sloid_unique
        unique (sloid, valid_from)
);

create table platform_version_info_opportunities
(
    platform_version_id bigint      not null,
    info_opportunities  varchar(50) not null
);

create table toilet_version
(
    id                         bigint           not null primary key,
    sloid                      varchar(500)     not null,
    number                     integer          not null,
    parent_service_point_sloid varchar(500)     not null,
    designation                varchar(50),
    additional_information     varchar(2000),
    wheelchair_toilet          varchar(50),
    valid_from                 date             not null,
    valid_to                   date             not null,
    creation_date              timestamp        not null,
    creator                    varchar(50)      not null,
    edition_date               timestamp        not null,
    editor                     varchar(50)      not null,
    version                    bigint default 0 not null,
    constraint toilet_sloid_unique
        unique (sloid, valid_from)
);

create table contact_point_version
(
    id                         bigint           not null primary key,
    sloid                      varchar(500)     not null,
    number                     integer          not null,
    parent_service_point_sloid varchar(500)     not null,
    designation                varchar(50),
    additional_information     varchar(2000),
    induction_loop             varchar(50),
    opening_hours              varchar(2000),
    wheelchair_access          varchar(50),
    type                       varchar(50)      not null,
    valid_from                 date             not null,
    valid_to                   date             not null,
    creation_date              timestamp        not null,
    creator                    varchar(50)      not null,
    edition_date               timestamp        not null,
    editor                     varchar(50)      not null,
    version                    bigint default 0 not null,
    constraint contact_point_sloid_unique
        unique (sloid, valid_from)
);

create table parking_lot_version
(
    id                         bigint           not null primary key,
    sloid                      varchar(500)     not null,
    number                     integer          not null,
    parent_service_point_sloid varchar(500)     not null,
    designation                varchar(50),
    additional_information     varchar(2000),
    places_available           varchar(50),
    prm_places_available       varchar(50),
    valid_from                 date             not null,
    valid_to                   date             not null,
    creation_date              timestamp        not null,
    creator                    varchar(50)      not null,
    edition_date               timestamp        not null,
    editor                     varchar(50)      not null,
    version                    bigint default 0 not null,
    constraint parking_lot_sloid_unique
        unique (sloid, valid_from)
);

create table relation_version
(
    id                           bigint           not null primary key,
    sloid                        varchar(500)     not null,
    number                       integer          not null,
    parent_service_point_sloid   varchar(500)     not null,
    tactile_visual_marks         varchar(50),
    contrasting_areas            varchar(50),
    step_free_access             varchar(50),
    reference_point_element_type varchar(50),
    valid_from                   date             not null,
    valid_to                     date             not null,
    creation_date                timestamp        not null,
    creator                      varchar(50)      not null,
    edition_date                 timestamp        not null,
    editor                       varchar(50)      not null,
    version                      bigint default 0 not null,
    reference_point_sloid        varchar(500)     not null,
    constraint relation_sloids_unique
        unique (sloid, reference_point_sloid, valid_from)
);

create table shared_service_point
(
    sloid         varchar(500)  not null primary key,
    service_point varchar(5000) not null
);

