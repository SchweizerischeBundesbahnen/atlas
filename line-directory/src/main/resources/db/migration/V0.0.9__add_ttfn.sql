CREATE TABLE timetable_field_number_version
(
    id                           BIGINT       NOT NULL PRIMARY KEY,
    ttfnid                       VARCHAR(500) NOT NULL,
    description                  VARCHAR(255),
    number                       VARCHAR(50)  NOT NULL,
    swiss_timetable_field_number VARCHAR(50)  NOT NULL,
    status                       VARCHAR(50)  NOT NULL,
    creation_date                TIMESTAMP    NOT NULL,
    creator                      VARCHAR(50)  NOT NULL,
    edition_date                 TIMESTAMP    NOT NULL,
    editor                       VARCHAR(50)  NOT NULL,
    valid_from                   DATE         NOT NULL,
    valid_to                     DATE         NOT NULL,
    business_organisation        VARCHAR(50)  NOT NULL,
    comment                      VARCHAR(250),
    version                      BIGINT       NOT NULL DEFAULT 0
);

CREATE SEQUENCE timetable_field_number_version_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE timetable_field_line_relation
(
    id                         BIGINT NOT NULL PRIMARY KEY,
    slnid                      VARCHAR(500),
    timetable_field_version_id BIGINT,
    CONSTRAINT fk_timetable_field_number_version
        FOREIGN KEY (timetable_field_version_id)
            REFERENCES timetable_field_number_version (id)
);

CREATE SEQUENCE timetable_field_line_relation_seq START WITH 1000 INCREMENT BY 1;

CREATE SEQUENCE ttfnid_seq START WITH 1000000 INCREMENT BY 1;

--- Create View
create or replace view timetable_field_number as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_timetable_field_number,
                                         description,
                                         status,
                                         ttfnid,
                                         valid_from as vf
                  from (
                           select distinct on (ttfnid) *
                           from ((select distinct on (ttfnid) *
                                  from timetable_field_number_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to)
                                 union all
                                 (select distinct on (ttfnid) *
                                  from timetable_field_number_version
                                  where valid_from >= current_timestamp
                                  order by ttfnid, valid_from asc)
                                 union all
                                 (select distinct on (ttfnid) *
                                  from timetable_field_number_version
                                  where valid_to <= current_timestamp
                                  order by ttfnid, valid_to desc)) as ranked
                       ) as chosen
              ) f
                  join (
             select ttfnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from timetable_field_number_version
             group by ttfnid
         ) v on f.ttfnid = v.ttfnid
     ) as timetable_field_numbers;