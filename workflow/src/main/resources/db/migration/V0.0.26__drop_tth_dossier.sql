-- TTH dossier data was migrated to the timetable-hearing service (ATLAS-3503)
DROP TABLE IF EXISTS tth_dossier_question;
DROP TABLE IF EXISTS tth_dossier_statement_ids;
DROP TABLE IF EXISTS tth_dossier;
DROP TABLE IF EXISTS tth_dossier_year;

DROP SEQUENCE IF EXISTS tth_dossier_question_seq;
DROP SEQUENCE IF EXISTS tth_dossier_seq;
