-----------------------------------------------------------------------------------------
-- Linienmodell 3.0 (ATLAS-3573) - manual test data for the V3 read API
--
-- NOT a Flyway migration. Run it by hand against a running line-directory DB:
--   docker compose up -d lidi-db
--   psql "postgresql://user:pwd@localhost:5433/line-directory" -v ON_ERROR_STOP=1 \
--        -f line-directory/src/test/resources/db/testdata/line_version_v3_testdata.sql
--
-- Idempotent: deletes its own rows (id range 900000-900999) before inserting.
--
-- Covers:
--   LINE      ORDERLY / ZONE / CONCESSION with own concession_type
--   LINE      multiple versions (past / today / future) -> getPrioritizedVersion
--   LINE      REVOKED parent -> still resolvable on the read path
--   SUBLINE   TECHNICAL / DISPOSITION / TEMPORARY / OPERATIONAL -> inherits concession_type
--   SUBLINE   ORDERLY / ZONE -> keeps its own concession_type
--   SUBLINE   dangling referenced_line_slnid -> referencedLine = null
--   SUBLINE   nesting depth 2 -> only the direct parent is exposed
--   Seilbahn  cableway_number / cableway_type / cableway_name
--   Status    DRAFT / IN_REVIEW / VALIDATED / REVOKED for the status filter
--   SUBLINE   CONCESSION with own swiss_line_number (overlap check) + member ordering
-----------------------------------------------------------------------------------------

DELETE FROM line_version_v3_means_of_transport WHERE line_version_v3_id BETWEEN 900000 AND 900999;
DELETE FROM line_version_v3 WHERE id BETWEEN 900000 AND 900999;

-----------------------------------------------------------------------------------------
-- 1) LINE / ORDERLY - three versions, exercises the prioritized-version selection
--    900001 past, 900002 valid today, 900003 future
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900001, 'ch:1:slnid:9000001', NULL, 'ORDERLY', 'FEDERALLY_LICENSED_LINE', 'S1', 'b1.S1', '11', 'S',
        'S1 Bern - Thun (alt)', 'Bern - Thun', '1001', 'Vorgaengerversion',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        CURRENT_DATE - INTERVAL '5 years', CURRENT_DATE - INTERVAL '1 year', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900002, 'ch:1:slnid:9000001', NULL, 'ORDERLY', 'FEDERALLY_LICENSED_LINE', 'S1', 'b1.S1', '11', 'S',
        'S1 Bern - Thun', 'Bern - Thun', '1001', 'Aktuell gueltige Version',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        CURRENT_DATE - INTERVAL '1 year' + INTERVAL '1 day', CURRENT_DATE + INTERVAL '1 year', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900003, 'ch:1:slnid:9000001', NULL, 'ORDERLY', 'CANTONALLY_APPROVED_LINE', 'S1', 'b1.S1', '11', 'S',
        'S1 Bern - Thun (neu)', 'Bern - Thun', '1001', 'Zukuenftige Version',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        CURRENT_DATE + INTERVAL '1 year' + INTERVAL '1 day', DATE '9999-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900001, 'TRAIN'),
       (900002, 'TRAIN'),
       (900002, 'BUS'),
       (900003, 'TRAIN');

-----------------------------------------------------------------------------------------
-- 2) LINE / ZONE - Gebietslinie with its own transport right
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900010, 'ch:1:slnid:9000010', NULL, 'ZONE', 'FEDERAL_ZONE_CONCESSION', 'S5', 'b1.S5', '61', 'S',
        'S5 Gebietskonzession', 'Bern - Thun Gebietskonzession', '1005', NULL,
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900010, 'TRAIN'),
       (900010, 'BUS');

-----------------------------------------------------------------------------------------
-- 3) SUBLINE - holds no transport right -> concessionType resolved from the parent
--    900020 TECHNICAL   -> FEDERALLY_LICENSED_LINE (from 900002, the version valid today)
--    900021 DISPOSITION -> FEDERALLY_LICENSED_LINE, the own stored value is overruled
--    900022 TEMPORARY   -> FEDERAL_ZONE_CONCESSION (from 900010)
--    900023 OPERATIONAL -> FEDERAL_ZONE_CONCESSION
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900020, 'ch:1:slnid:9000020', 'ch:1:slnid:9000001', 'TECHNICAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Bern - Thun technische Linie', NULL, 'Erbt concessionType vom Parent',
        'ch:1:sboid:100001', 'WBV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900021, 'ch:1:slnid:9000021', 'ch:1:slnid:9000001', 'DISPOSITION', 'STATE_TREATY', NULL, NULL,
        NULL, NULL, NULL, 'Bern - Thun Dispositionslinie', NULL, 'Eigener Wert wird vom Parent ueberschrieben',
        'ch:1:sboid:100001', 'WBV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900022, 'ch:1:slnid:9000022', 'ch:1:slnid:9000010', 'TEMPORARY', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Bern - Thun temporaere Linie', NULL, NULL,
        'ch:1:sboid:100001', NULL, NULL, NULL, NULL,
        DATE '2026-01-01', DATE '2026-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900023, 'ch:1:slnid:9000023', 'ch:1:slnid:9000010', 'OPERATIONAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Bern - Thun Betriebspunktlinie', NULL, NULL,
        'ch:1:sboid:100002', 'SELF_FINANCED', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900020, 'BUS'),
       (900021, 'BUS'),
       (900022, 'TRAM'),
       (900023, 'METRO');

-----------------------------------------------------------------------------------------
-- 4) SUBLINE - ORDERLY / ZONE -> keeps its own transport right
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900030, 'ch:1:slnid:9000030', 'ch:1:slnid:9000001', 'ORDERLY', 'STATE_TREATY', 'S1X', 'b1.S1X',
        '12', 'S', NULL, 'Bern - Thun ordentliche Teillinie', NULL, 'Eigener concessionType bleibt erhalten',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900031, 'ch:1:slnid:9000031', 'ch:1:slnid:9000010', 'ZONE', 'RIGHT_FREE_LINE', 'S5Z', 'b1.S5Z',
        '62', 'S', NULL, 'Bern - Thun Teilgebiet', NULL, 'Eigener concessionType bleibt erhalten',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900030, 'TRAIN'),
       (900031, 'BUS');

-----------------------------------------------------------------------------------------
-- 5) SUBLINE - dangling reference -> referencedLine = null, own values stay untouched
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900040, 'ch:1:slnid:9000040', 'ch:1:slnid:9999999', 'TECHNICAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Teillinie ohne auffindbaren Parent', NULL, 'referencedLine bleibt null',
        'ch:1:sboid:100001', NULL, NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900040, 'BUS');

-----------------------------------------------------------------------------------------
-- 6) SUBLINE of a SUBLINE - nesting depth stays 1, only the direct parent is exposed
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900050, 'ch:1:slnid:9000050', 'ch:1:slnid:9000020', 'TECHNICAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Teillinie einer Teillinie', NULL, 'Parent ist selbst eine SUBLINE',
        'ch:1:sboid:100001', NULL, NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900050, 'BUS');

-----------------------------------------------------------------------------------------
-- 7) REVOKED parent with a SUBLINE - the read path applies no status filter on resolution
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900060, 'ch:1:slnid:9000060', NULL, 'ORDERLY', 'FEDERALLY_APPROVED_LINE', '90.583', '90.583',
        '583', 'B', NULL, 'Aufgehobene Linie', NULL, NULL,
        'ch:1:sboid:100003', 'RPV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'REVOKED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900061, 'ch:1:slnid:9000061', 'ch:1:slnid:9000060', 'CONCESSION', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Teillinie einer aufgehobenen Linie', NULL, 'Parent REVOKED, trotzdem aufloesbar',
        'ch:1:sboid:100003', NULL, NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900060, 'BUS'),
       (900061, 'BUS');

-----------------------------------------------------------------------------------------
-- 8) Seilbahn - cableway attributes (Anlagennummer, Anlage-Typ, Anlage-Name)
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900070, 'ch:1:slnid:9000070', NULL, 'ORDERLY', 'FEDERALLY_LICENSED_LINE', 'SB1', 'b1.SB1', '71', 'B',
        'Seilbahn Wengen - Maennlichen', 'Wengen - Maennlichen', '1070', 'Pendelbahn mit Anlagedaten',
        'ch:1:sboid:100004', 'SELF_FINANCED', '123', 'PB', 'Luftseilbahn Wengen-Maennlichen',
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900071, 'ch:1:slnid:9000071', 'ch:1:slnid:9000070', 'TECHNICAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Sesselbahn Zubringer', NULL, NULL,
        'ch:1:sboid:100004', 'SELF_FINANCED', '124', 'SB', 'Sesselbahn Zubringer Nord',
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900070, 'AERIAL_TRAMWAY'),
       (900070, 'GONDOLA_LIFT'),
       (900071, 'CHAIRLIFT');

-----------------------------------------------------------------------------------------
-- 9) DRAFT / IN_REVIEW rows - status filter on the list endpoint
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900080, 'ch:1:slnid:9000080', NULL, 'ORDERLY', 'RIGHT_FREE_LINE', 'S9', 'b1.S9', '91', 'S',
        NULL, 'Linie im Entwurf', NULL, NULL,
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2026-01-01', DATE '2099-12-31', 'DRAFT',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900081, 'ch:1:slnid:9000081', NULL, 'ORDERLY', 'RIGHT_FREE_LINE', 'S10', 'b1.S10', '101', 'S',
        NULL, 'Linie in Pruefung', NULL, NULL,
        'ch:1:sboid:100001', 'LONG_DISTANCE_SERVICES', NULL, NULL, NULL,
        DATE '2026-01-01', DATE '2099-12-31', 'IN_REVIEW',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900080, 'TRAIN'),
       (900081, 'TRAIN');

-----------------------------------------------------------------------------------------
-- 10) LINE with CONCESSION / TECHNICAL members (ATLAS-3578, write repository queries)
--     900091/900092 CONCESSION member with own swiss_line_number, two versions
--     900093        TECHNICAL member, starts before the CONCESSION member (ordering)
-----------------------------------------------------------------------------------------
INSERT INTO line_version_v3 (id, slnid, referenced_line_slnid, line_type, concession_type, number, swiss_line_number,
                             short_number, offer_category, long_name, description, linien_id, comment,
                             business_organisation, financing, cableway_number, cableway_type, cableway_name,
                             valid_from, valid_to, status, creation_date, creator, edition_date, editor, version)
VALUES (900090, 'ch:1:slnid:9000090', NULL, 'ORDERLY', 'FEDERALLY_LICENSED_LINE', 'K', 'b1.K', '90', 'B',
        NULL, 'Linie mit Konzessionsteillinie', NULL, NULL,
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2023-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900091, 'ch:1:slnid:9000091', 'ch:1:slnid:9000090', 'CONCESSION', 'CANTONALLY_APPROVED_LINE', NULL, 'b1.K1',
        NULL, NULL, NULL, 'Konzessionsteillinie', NULL, 'Version 1',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2024-01-01', DATE '2024-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900092, 'ch:1:slnid:9000091', 'ch:1:slnid:9000090', 'CONCESSION', 'CANTONALLY_APPROVED_LINE', NULL, 'b1.K1',
        NULL, NULL, NULL, 'Konzessionsteillinie', NULL, 'Version 2',
        'ch:1:sboid:100001', 'RPV', NULL, NULL, NULL,
        DATE '2025-01-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0),
       (900093, 'ch:1:slnid:9000093', 'ch:1:slnid:9000090', 'TECHNICAL', NULL, NULL, NULL,
        NULL, NULL, NULL, 'Technische Teillinie', NULL, NULL,
        'ch:1:sboid:100001', NULL, NULL, NULL, NULL,
        DATE '2023-06-01', DATE '2099-12-31', 'VALIDATED',
        NOW(), 'testdata', NOW(), 'testdata', 0);

INSERT INTO line_version_v3_means_of_transport (line_version_v3_id, means_of_transport)
VALUES (900090, 'BUS'),
       (900091, 'BUS'),
       (900092, 'BUS'),
       (900093, 'BUS');

-----------------------------------------------------------------------------------------
-- Verification
-----------------------------------------------------------------------------------------
SELECT (SELECT COUNT(*) FROM line_version_v3
         WHERE id BETWEEN 900000 AND 900999)                       AS rows_total,
       (SELECT COUNT(*) FROM line_version_v3
         WHERE id BETWEEN 900000 AND 900999
           AND referenced_line_slnid IS NULL)                      AS lines,
       (SELECT COUNT(*) FROM line_version_v3
         WHERE id BETWEEN 900000 AND 900999
           AND referenced_line_slnid IS NOT NULL)                  AS sublines,
       (SELECT COUNT(*) FROM line_version_v3_means_of_transport
         WHERE line_version_v3_id BETWEEN 900000 AND 900999)       AS means_of_transport_rows;
