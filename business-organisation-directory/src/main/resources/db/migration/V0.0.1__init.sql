-----------------------------------------------------------------------------------------
-- Business Organisation
-----------------------------------------------------------------------------------------

CREATE TABLE business_organisation_version
(
    id                           BIGINT       NOT NULL PRIMARY KEY,
    sboid                        VARCHAR(32)  NOT NULL,
    abbreviation_de              VARCHAR(10)  NOT NULL,
    abbreviation_fr              VARCHAR(10)  NOT NULL,
    abbreviation_it              VARCHAR(10)  NOT NULL,
    abbreviation_en              VARCHAR(10)  NOT NULL,
    description_de               VARCHAR(60)  NOT NULL,
    description_fr               VARCHAR(60)  NOT NULL,
    description_it               VARCHAR(60)  NOT NULL,
    description_en               VARCHAR(60)  NOT NULL,
    organisation_number          INTEGER      NOT NULL,
    contact_enterprise_email     VARCHAR(255),
    status                       VARCHAR(50)  NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    version                      BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE business_organisation_version_seq START WITH 1000 INCREMENT BY 1;

CREATE SEQUENCE sboid_seq START WITH 1100000 INCREMENT BY 1;

CREATE TABLE business_organisation_version_business_types
(
    business_organisation_version_id    BIGINT NOT NULL,
    business_types                      VARCHAR(50)  NOT NULL
)

