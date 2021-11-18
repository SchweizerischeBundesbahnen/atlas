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
                           from ((select swiss_line_number,
                                         number,
                                         description,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from line_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to)
                                 union all
                                 (select swiss_line_number,
                                         number,
                                         description,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from line_version
                                  where valid_from >= current_timestamp
                                  order by valid_from)
                                 union all
                                 (select swiss_line_number,
                                         number,
                                         description,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from line_version
                                  where valid_to <= current_timestamp
                                  order by valid_to desc)) as ranked
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
                           from ((select swiss_subline_number,
                                         description,
                                         swiss_line_number,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from subline_version
                                  where valid_from <= current_timestamp
                                    and current_timestamp <= valid_to)
                                 union all
                                 (select swiss_subline_number,
                                         description,
                                         swiss_line_number,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from subline_version
                                  where valid_from >= current_timestamp
                                  order by valid_from)
                                 union all
                                 (select swiss_subline_number,
                                         description,
                                         swiss_line_number,
                                         status,
                                         type,
                                         business_organisation,
                                         slnid
                                  from subline_version
                                  where valid_to <= current_timestamp
                                  order by valid_to desc)) as ranked
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from subline_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as sublines;

-- It has already long_names in db from initial_data-script, which are longer than 255 chars
update line_version set long_name = substr(long_name, 1, 255) where length(long_name) > 255;
update subline_version set long_name = substr(long_name, 1, 255) where length(long_name) > 255;
-- For safety, if it has updated records, since the initial_data-import
update line_version set combination_name = substr(combination_name, 1, 50) where length(combination_name) > 50;

alter table line_version
    alter column long_name type varchar(255);
alter table subline_version
    alter column long_name type varchar(255);
alter table line_version
    alter column combination_name type varchar(50);
