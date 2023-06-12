-- Data Shared via Kafka to this service

-- Business Organisation Version
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

-- Transport Company
create table shared_transport_company
(
    id                       bigint primary key,
    number                   varchar(50),
    abbreviation             varchar(50),
    description              varchar(200),
    business_register_name   varchar(950),
    business_register_number varchar(50)
);