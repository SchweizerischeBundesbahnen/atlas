insert into available_service_point_numbers
select available_numbers, 'SWITZERLAND' as country
from generate_series(1, 99999) as available_numbers
         left outer join (select *
                          from service_point_version as spv1
                          where country in ('SWITZERLAND')) as spv
                         on available_numbers = spv.number_short
where spv.id is null;

insert into available_service_point_numbers
select available_numbers, 'GERMANY_BUS' as country
from generate_series(1, 99999) as available_numbers
         left outer join (select *
                          from service_point_version as spv1
                          where country in ('GERMANY_BUS')) as spv
                         on available_numbers = spv.number_short
where spv.id is null;

insert into available_service_point_numbers
select available_numbers, 'AUSTRIA_BUS' as country
from generate_series(1, 99999) as available_numbers
         left outer join (select *
                          from service_point_version as spv1
                          where country in ('AUSTRIA_BUS')) as spv
                         on available_numbers = spv.number_short
where spv.id is null;

insert into available_service_point_numbers
select available_numbers, 'ITALY_BUS' as country
from generate_series(1, 99999) as available_numbers
         left outer join (select *
                          from service_point_version as spv1
                          where country in ('ITALY_BUS')) as spv
                         on available_numbers = spv.number_short
where spv.id is null;

insert into available_service_point_numbers
select available_numbers, 'FRANCE_BUS' as country
from generate_series(1, 99999) as available_numbers
         left outer join (select *
                          from service_point_version as spv1
                          where country in ('FRANCE_BUS')) as spv
                         on available_numbers = spv.number_short
where spv.id is null;
