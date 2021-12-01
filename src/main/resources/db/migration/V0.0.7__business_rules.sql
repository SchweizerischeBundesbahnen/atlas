CREATE SEQUENCE ttfnid_seq START WITH 1000000 INCREMENT BY 1;

DELETE
from timetable_field_number_version
where number IS NULL;
ALTER TABLE timetable_field_number_version
    alter column number SET not null;

DELETE
from timetable_field_number_version
where status IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN status set NOT NULL;

DELETE
from timetable_field_number_version
where creation_date IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN creation_date set NOT NULL;

DELETE
from timetable_field_number_version
where edition_date IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN edition_date set NOT NULL;

DELETE
from timetable_field_number_version
where creator IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN creator set NOT NULL;

DELETE
from timetable_field_number_version
where editor IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN editor set NOT NULL;

DELETE
from timetable_field_number_version
where swiss_timetable_field_number IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN swiss_timetable_field_number set NOT NULL;

DELETE
from timetable_field_number_version
where ttfnid IS NULL;
ALTER TABLE timetable_field_number_version
    ALTER COLUMN ttfnid set NOT NULL;

DELETE
from timetable_field_number_version
where valid_to IS NULL;
ALTER TABLE timetable_field_number_version
    alter column valid_to set not null;

DELETE
from timetable_field_number_version
where valid_from IS NULL;
ALTER TABLE timetable_field_number_version
    alter column valid_from set not null;

update timetable_field_number_version
set business_organisation = '-'
where business_organisation is null;
alter table timetable_field_number_version
    alter column business_organisation set not null;

drop view timetable_field_number;

update timetable_field_number_version
set name = substr(name, 1, 255)
where length(name) > 255;
alter table timetable_field_number_version
    alter column name type varchar(255);

create or replace view timetable_field_number as
with timetable_field_numbers_fields as (
    with valid_today as (
        select distinct on (ttfnid) swiss_timetable_field_number, name, status, ttfnid
        from timetable_field_number_version
        where valid_from <= current_timestamp
          and current_timestamp <= valid_to
    ),
         valid_in_future as (
             select distinct on (ttfnid) swiss_timetable_field_number,
                                         name,
                                         status,
                                         ttfnid,
                                         valid_from as vf
             from timetable_field_number_version
             where valid_from >= current_timestamp
             order by ttfnid, valid_from asc
         ),
         last_valid_in_past as (
             select distinct on (ttfnid) swiss_timetable_field_number,
                                         name,
                                         status,
                                         ttfnid,
                                         valid_to as vt
             from timetable_field_number_version
             where valid_to <= current_timestamp
             order by ttfnid, valid_to desc
         ),
         ranked_properties as (
             select swiss_timetable_field_number, name, status, ttfnid
             from valid_today
             union all
             select swiss_timetable_field_number, name, status, ttfnid
             from valid_in_future
             union all
             select swiss_timetable_field_number, name, status, ttfnid
             from last_valid_in_past
         ),
         chosen_one as (
             select distinct on (ttfnid) *
             from ranked_properties
         )
    select swiss_timetable_field_number, name, status, ttfnid
    from chosen_one
),
     timetable_field_number_validity as (
         select ttfnid, min(valid_from) as valid_from, max(valid_to) as valid_to
         from timetable_field_number_version
         group by ttfnid
     ),
     timetable_field_numbers as (
         select f.*, v.valid_from, v.valid_to
         from timetable_field_numbers_fields f
                  join timetable_field_number_validity v on f.ttfnid = v.ttfnid
     )
select *
from timetable_field_numbers
