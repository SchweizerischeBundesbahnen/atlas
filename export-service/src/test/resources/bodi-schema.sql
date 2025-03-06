-- Business Organisation
create table business_organisation_version
(
    id                       bigint           not null
        primary key,
    sboid                    varchar(32)      not null,
    abbreviation_de          varchar(10)      not null,
    abbreviation_fr          varchar(10)      not null,
    abbreviation_it          varchar(10)      not null,
    abbreviation_en          varchar(10)      not null,
    description_de           varchar(60)      not null,
    description_fr           varchar(60)      not null,
    description_it           varchar(60)      not null,
    description_en           varchar(60)      not null,
    organisation_number      integer          not null,
    contact_enterprise_email varchar(255),
    status                   varchar(50)      not null,
    valid_from               date             not null,
    valid_to                 date             not null,
    creation_date            timestamp        not null,
    creator                  varchar(50)      not null,
    edition_date             timestamp        not null,
    editor                   varchar(50)      not null,
    version                  bigint default 0 not null
);

create table business_organisation_version_business_types
(
    business_organisation_version_id bigint      not null,
    business_types                   varchar(50) not null
);

-- Transport Company
create table transport_company
(
    id                            bigint    not null primary key,
    number                        varchar(50),
    abbreviation                  varchar(50),
    description                   varchar(200),
    business_register_name        varchar(950),
    transport_company_status      varchar(50),
    business_register_number      varchar(50),
    enterprise_id                 varchar(50),
    rics_code                     varchar(50),
    business_organisation_numbers varchar(250),
    comment                       varchar(500),
    creation_date                 timestamp not null,
    edition_date                  timestamp not null
);

create table transport_company_relation
(
    id                   bigint           not null
        primary key,
    transport_company_id bigint           not null
        references transport_company,
    sboid                varchar(32)      not null,
    valid_from           date             not null,
    valid_to             date             not null,
    creator              varchar(50)      not null,
    creation_date        timestamp        not null,
    editor               varchar(50)      not null,
    edition_date         timestamp        not null,
    version              bigint default 0 not null
);
