create table allocated_sloid
(
    sloid       varchar(128) primary key,
    sloidType   varchar(50)
);

create table available_service_point_sloid
(
    sloid     varchar(128),
    country   varchar(50),
    claimed   boolean not null default false,
    primary key (sloid, country)
);

create sequence area_seq start with 100;
create sequence edge_seq;

insert into available_service_point_sloid
select ('ch:1:sloid:' || available_sloids) as sloid, 'SWITZERLAND' as country
from generate_series(1, 99999) as available_sloids;

insert into available_service_point_sloid
select ('ch:1:sloid:' || available_sloids + 1100000) as sloid, 'GERMANY_BUS' as country
from generate_series(1, 99999) as available_sloids;

insert into available_service_point_sloid
select ('ch:1:sloid:' || available_sloids + 1200000) as sloid, 'AUSTRIA_BUS' as country
from generate_series(1, 99999 ) as available_sloids;

insert into available_service_point_sloid
select ('ch:1:sloid:' || available_sloids + 1300000) as sloid, 'ITALY_BUS' as country
from generate_series(1, 99999) as available_sloids;

insert into available_service_point_sloid
select ('ch:1:sloid:' || available_sloids + 1400000) as sloid, 'FRANCE_BUS' as country
from generate_series(1, 99999) as available_sloids;
