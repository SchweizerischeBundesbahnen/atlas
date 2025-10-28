drop view timetable_field_number;

alter table timetable_field_number_version
drop
column comment;

alter table timetable_field_number_version
    rename column description to description_outward_line_1;

alter table timetable_field_number_version
    add column description_outward_line_2 varchar(255);

alter table timetable_field_number_version
    add column description_outward_line_3 varchar(255);

alter table timetable_field_number_version
    add column description_return_line_1 varchar(255);

alter table timetable_field_number_version
    add column description_return_line_2 varchar(255);

alter table timetable_field_number_version
    add column description_return_line_3 varchar(255);

alter table timetable_field_number_version
    add column mean_of_transport varchar(50);

create or replace view timetable_field_number as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_timetable_field_number,
                         number,
                         description_outward_line_1,
                         status,
                         ttfnid,
                         business_organisation,
                         valid_from as vf
                  from (
                           select distinct on (ttfnid) *
                           from ((select distinct on (ttfnid) 1 as rank, *
                               from timetable_field_number_version
                               where valid_from <= current_date
                               and current_date <= valid_to)
                               union all
                               (select distinct on (ttfnid) 2 as rank, *
                               from timetable_field_number_version
                               where valid_from >= current_date
                               order by ttfnid, valid_from)
                               union all
                               (select distinct on (ttfnid) 3 as rank, *
                               from timetable_field_number_version
                               where valid_to <= current_date
                               order by ttfnid, valid_to desc)) as ranked order by ttfnid, rank
                       ) as chosen
              ) f
                  join (
             select ttfnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from timetable_field_number_version
             group by ttfnid
         ) v on f.ttfnid = v.ttfnid
     ) as timetable_field_numbers;
