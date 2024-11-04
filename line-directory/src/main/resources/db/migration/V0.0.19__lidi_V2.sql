-----------------------------------------------------------------------------------------
-- Subline View V2
-----------------------------------------------------------------------------------------
DROP VIEW subline;

create or replace view subline as
select *
from (
         select f.*, v.valid_from, v.valid_to
         from (
                  select swiss_subline_number,
                         description,
                         swiss_line_number,
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
                                                               s.status,
                                                               s.subline_type,
                                                               l.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from <= current_date
                                    and current_date <= s.valid_to)
                                 union all
                                 (select distinct on (s.slnid) 2 as rank,
                                                               s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.status,
                                                               s.subline_type,
                                                               l.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_from >= current_date
                                  order by s.slnid, s.valid_from)
                                 union all
                                 (select distinct on (s.slnid) 3 as rank,
                                                               s.swiss_subline_number,
                                                               s.description,
                                                               l.swiss_line_number,
                                                               s.status,
                                                               s.subline_type,
                                                               l.business_organisation,
                                                               s.slnid,
                                                               s.valid_from,
                                                               s.valid_to
                                  from subline_version s
                                           join line l on s.mainline_slnid = l.slnid
                                  where s.valid_to <= current_date
                                  order by s.slnid, s.valid_to desc)) as ranked order by slnid, rank
                       ) as chosen
              ) f
                  join (
             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
             from subline_version
             group by slnid
         ) v on f.slnid = v.slnid
     ) as sublines;


-----------------------------------------------------------------------------------------
-- LineVersion V2
-----------------------------------------------------------------------------------------

ALTER TABLE line_version
    ADD COLUMN concession_type varchar(50);
ALTER TABLE line_version
    ADD COLUMN offer_category varchar(50);
ALTER TABLE line_version
    ADD COLUMN short_number varchar(10);

-----------------------------------------------------------------------------------------
-- SublineVersion V2
-----------------------------------------------------------------------------------------

ALTER TABLE subline_version
    ADD COLUMN concession_type varchar(50);

-----------------------------------------------------------------------------------------
-- LineVersionSnapshot V2
-----------------------------------------------------------------------------------------

ALTER TABLE line_version_snapshot
    ADD COLUMN concession_type varchar(50);
ALTER TABLE line_version_snapshot
    ADD COLUMN offer_category varchar(50);
ALTER TABLE line_version_snapshot
    ADD COLUMN short_number varchar(10);

-----------------------------------------------------------------------------------------
-- Overview Line-Subline V2
-----------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW overview_line_subline as

select *
from (select number,
             description,
             line_type         as type,
             swiss_line_number as swiss_number,
             slnid,
             status,
             valid_from,
             valid_to
      from line lv
      union
      select '' as number,
             description,
             subline_type         as type,
             swiss_subline_number as swiss_number,
             slnid,
             status,
             valid_from,
             valid_to
      from subline sv) as lv;

