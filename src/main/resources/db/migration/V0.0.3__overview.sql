create view timetable_field_numbers_overview as
with timetable_field_numbers_fields as (
    with valid_today as (
        select *
        from timetable_field_number_version
        where valid_from <= current_timestamp
          and current_timestamp <= valid_to
    ),
         valid_in_future as (
             select *
             from timetable_field_number_version
             where valid_from >= current_timestamp
             order by valid_from
         ),
         last_valid_in_past as (
             select *
             from timetable_field_number_version
             where valid_to <= current_timestamp
             order by valid_to desc
         ),
         ranked_properties as (
             select *
             from valid_today
             union all
             select *
             from valid_in_future
             union all
             select *
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