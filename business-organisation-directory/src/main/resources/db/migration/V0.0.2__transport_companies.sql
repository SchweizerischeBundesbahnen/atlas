-----------------------------------------------------------------------------------------
-- Transport Company
-----------------------------------------------------------------------------------------

CREATE TABLE transport_company
(
    id                            BIGINT    NOT NULL PRIMARY KEY,
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
    creation_date                 TIMESTAMP NOT NULL,
    edition_date                  TIMESTAMP NOT NULL
);


