ALTER TABLE line_version
    ADD version BIGINT not null default 0;
ALTER TABLE subline_version
    ADD version BIGINT not null default 0;
