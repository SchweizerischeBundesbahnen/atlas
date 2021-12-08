ALTER TABLE subline_version
    rename column swiss_line_number to mainline_slnid;
ALTER TABLE subline_version
    ALTER COLUMN mainline_slnid set NOT NULL;

create or replace view subline as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_subline_number,
                         description,
                         swiss_line_number,
                         status,
                         type,
                         business_organisation,
                         slnid
                  from (
                           select distinct on (slnid) *
                           from ((select distinct on (s.slnid) s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.status,
                                                               s.type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from <= current_timestamp
                                    and current_timestamp <= s.valid_to)
                                 union all
                                 (select distinct on (s.slnid) s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.status,
                                                               s.type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from >= current_timestamp
                                  order by s.slnid, s.valid_from asc)
                                 union all
                                 (select distinct on (s.slnid) s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.status,
                                                               s.type,
                                                               s.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_to <= current_timestamp
                                  order by s.slnid, s.valid_to desc)) as ranked
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from subline_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as sublines;
