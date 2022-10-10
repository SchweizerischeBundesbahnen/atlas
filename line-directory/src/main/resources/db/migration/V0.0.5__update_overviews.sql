drop view timetable_field_number;
drop view subline;
drop view line;


--- TTFN
create or replace view timetable_field_number as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_timetable_field_number,
                         number,
                         description,
                         status,
                         ttfnid,
                         business_organisation,
                         valid_from as vf
                  from (
                           select distinct on (ttfnid) *
                           from ((select distinct on (ttfnid) 1 as rank, *
                                  from timetable_field_number_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to::date+1)
                                 union all
                                 (select distinct on (ttfnid) 2 as rank, *
                                  from timetable_field_number_version
                                  where valid_from >= current_timestamp
                                  order by ttfnid, valid_from)
                                 union all
                                 (select distinct on (ttfnid) 3 as rank, *
                                  from timetable_field_number_version
                                  where valid_to <= current_timestamp
                                  order by ttfnid, valid_to desc)) as ranked order by ttfnid, rank
                       ) as chosen
              ) f
                  join (
             select ttfnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from timetable_field_number_version
             group by ttfnid
         ) v on f.ttfnid = v.ttfnid
     ) as timetable_field_numbers;

-- Line
create or replace view line as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_line_number,
                         number,
                         description,
                         status,
                         line_type,
                         business_organisation,
                         slnid
                  from (
                           select distinct on (slnid) *
                           from ((select distinct on (slnid) 1 as rank,
                                                             swiss_line_number,
                                                             number,
                                                             description,
                                                             status,
                                                             line_type,
                                                             business_organisation,
                                                             slnid,
                                                             valid_from,
                                                             valid_to
                                  from line_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to::date+1)
                                 union all
                                 (select distinct on (slnid) 2 as rank,
                                                             swiss_line_number,
                                                             number,
                                                             description,
                                                             status,
                                                             line_type,
                                                             business_organisation,
                                                             slnid,
                                                             valid_from,
                                                             valid_to
                                  from line_version
                                  where valid_from >= current_timestamp
                                  order by slnid, valid_from)
                                 union all
                                 (select distinct on (slnid) 3 as rank,
                                                             swiss_line_number,
                                                             number,
                                                             description,
                                                             status,
                                                             line_type,
                                                             business_organisation,
                                                             slnid,
                                                             valid_from,
                                                             valid_to
                                  from line_version
                                  where valid_to <= current_timestamp
                                  order by slnid, valid_to desc)) as ranked order by slnid, rank
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from line_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as lines;

-- Subline
create or replace view subline as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_subline_number,
                         description,
                         swiss_line_number,
                         number,
                         status,
                         subline_type,
                         business_organisation,
                         slnid
                  from (
                           select distinct on (slnid) *
                           from ((select distinct on (s.slnid) 1 as rank,
                                                               s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.number,
                                                               s.status,
                                                               s.subline_type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from <= current_timestamp
                                    and current_timestamp <= s.valid_to::date+1)
                                 union all
                                 (select distinct on (s.slnid) 2 as rank,
                                                               s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.number,
                                                               s.status,
                                                               s.subline_type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from >= current_timestamp
                                  order by s.slnid, s.valid_from)
                                 union all
                                 (select distinct on (s.slnid) 3 as rank,
                                                               s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.number,
                                                               s.status,
                                                               s.subline_type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_to <= current_timestamp
                                  order by s.slnid, s.valid_to desc)) as ranked order by slnid, rank
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from subline_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as sublines;