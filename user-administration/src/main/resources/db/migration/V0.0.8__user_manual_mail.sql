CREATE TABLE user_manual_mail
(
    id            bigint primary key,
    sbb_user_id   VARCHAR(50)  NOT NULL,
    mail          VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    creator       VARCHAR(50)  NOT NULL,
    edition_date  TIMESTAMP    NOT NULL,
    editor        VARCHAR(50)  NOT NULL,
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT user_manual_mail_sbb_user_id_unique UNIQUE (sbb_user_id)
);

CREATE INDEX user_manual_mail_mail_idx ON user_manual_mail (mail);

CREATE SEQUENCE user_manual_mail_seq START WITH 1000 INCREMENT BY 1;
