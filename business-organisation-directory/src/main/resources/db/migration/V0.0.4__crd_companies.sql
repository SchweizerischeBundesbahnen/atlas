create table company
(
    uic_code            bigint primary key,
    name                varchar(255),
    name_ascii          varchar(255),
    url                 varchar(255),
    start_validity      timestamp,
    end_validity        timestamp,
    short_name          varchar(50),
    free_text           varchar(500),
    country_code_iso    varchar(10),
    passenger_flag      boolean,
    freight_flag        boolean,
    infrastructure_flag boolean,
    other_company_flag  boolean,
    ne_entity_flag      boolean,
    ce_entity_flag      boolean,
    add_date            timestamp,
    modified_date       timestamp,
    creation_date       TIMESTAMP NOT NULL,
    edition_date        TIMESTAMP NOT NULL
);