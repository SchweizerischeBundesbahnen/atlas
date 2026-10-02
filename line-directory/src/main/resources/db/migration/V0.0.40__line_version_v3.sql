-----------------------------------------------------------------------------------------
-- Linienmodell 3.0 - Read contract V3 (ATLAS-3573)
-- Unified line/subline version table. The LINE/SUBLINE discriminator is derived at read
-- time from referenced_line_slnid (NULL -> LINE, NOT NULL -> SUBLINE).
-- No foreign key on referenced_line_slnid by design: dangling references are tolerated
-- and resolve to referencedLine = null.
-- cableway_number / cableway_type / cableway_name (Anlagennummer, Anlage-Typ, Anlage-Name)
-- carry values only for cableway means of transport; the restriction is not enforced yet.
-----------------------------------------------------------------------------------------

CREATE TABLE line_version_v3
(
    id                    BIGINT       NOT NULL PRIMARY KEY,
    slnid                 VARCHAR(500) NOT NULL,
    referenced_line_slnid VARCHAR(500),
    line_type             VARCHAR(50)  NOT NULL,
    concession_type       VARCHAR(50),
    number                VARCHAR(50),
    swiss_line_number     VARCHAR(50),
    short_number          VARCHAR(10),
    offer_category        VARCHAR(50),
    long_name             VARCHAR(255),
    description           VARCHAR(255) NOT NULL,
    linien_id             VARCHAR(20),
    comment               VARCHAR(1500),
    business_organisation VARCHAR(50)  NOT NULL,
    financing             VARCHAR(50),
    cableway_number       VARCHAR(50),
    cableway_type         VARCHAR(50),
    cableway_name         VARCHAR(255),
    valid_from            DATE         NOT NULL,
    valid_to              DATE         NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    creation_date         TIMESTAMP    NOT NULL,
    creator               VARCHAR(50),
    edition_date          TIMESTAMP    NOT NULL,
    editor                VARCHAR(50),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE line_version_v3_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE line_version_v3 ADD CONSTRAINT line_v3_slnid_unique UNIQUE (slnid, valid_from);

CREATE INDEX idx_line_version_v3_referenced_line_slnid
    ON line_version_v3 (referenced_line_slnid);

-----------------------------------------------------------------------------------------
-- Line V3 - MeansOfTransport - Verkehrsmittel
-- Mirrors service_point_version_means_of_transport.
-----------------------------------------------------------------------------------------

CREATE TABLE line_version_v3_means_of_transport
(
    line_version_v3_id BIGINT      NOT NULL,
    means_of_transport VARCHAR(50) NOT NULL
);

ALTER TABLE line_version_v3_means_of_transport ADD CONSTRAINT fk_means_of_transport_line_version_v3
    FOREIGN KEY (line_version_v3_id) REFERENCES line_version_v3 (id);

CREATE INDEX idx_line_version_v3_means_of_transport_line_version_v3_id
    ON line_version_v3_means_of_transport (line_version_v3_id);
