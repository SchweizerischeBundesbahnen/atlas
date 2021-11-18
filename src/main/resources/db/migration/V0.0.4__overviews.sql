create or replace view line as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_line_number,
                         number,
                         description,
                         status,
                         type,
                         business_organisation,
                         slnid
                  from (
                           select distinct on (slnid) *
                           from ((select distinct on (slnid) *
                                  from line_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to)
                                 union all
                                 (select distinct on (slnid) *
                                  from line_version
                                  where valid_from >= current_timestamp
                               order by slnid, valid_from asc)
                                 union all
                                 (select distinct on (slnid) *
                                  from line_version
                                  where valid_to <= current_timestamp
                                  order by slnid, valid_to desc)) as ranked
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from line_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as lines;

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
                           from ((select distinct on (slnid) *
                               from subline_version
                               where valid_from <= current_timestamp
                               and current_timestamp <= valid_to)
                               union all
                               (select distinct on (slnid) *
                               from subline_version
                               where valid_from >= current_timestamp
                               order by slnid, valid_from asc)
                               union all
                               (select distinct on (slnid) *
                               from subline_version
                               where valid_to <= current_timestamp
                               order by slnid, valid_to desc)) as ranked
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from subline_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as sublines;
