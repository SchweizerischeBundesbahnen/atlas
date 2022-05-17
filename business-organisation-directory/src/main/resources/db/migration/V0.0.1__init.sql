-----------------------------------------------------------------------------------------
-- Business Organisation
-----------------------------------------------------------------------------------------

CREATE TABLE business_organisation_version
(
    id                           BIGINT       NOT NULL PRIMARY KEY,
    sboid                        VARCHAR(500) NOT NULL,
    said                         VARCHAR(255),
    description_de               VARCHAR(255),
    description_fr               VARCHAR(255),
    description_it               VARCHAR(255),
    description_en               VARCHAR(255),
    abbreviation_de              VARCHAR(50),
    abbreviation_fr              VARCHAR(50),
    abbreviation_it              VARCHAR(50),
    abbreviation_en              VARCHAR(50),
    organisation_number          INTEGER,
    contact_enterprise_email     VARCHAR(255),
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE business_organisation_version_seq START WITH 1000 INCREMENT BY 1;

CREATE SEQUENCE sboid_seq START WITH 1000000 INCREMENT BY 1;

