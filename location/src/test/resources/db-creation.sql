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

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n)) as sloid, 'SWITZERLAND' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n) + 1100000) as sloid, 'GERMANY_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n) + 1200000) as sloid, 'AUSTRIA_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n) + 1300000) as sloid, 'ITALY_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n) + 1400000) as sloid, 'FRANCE_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;
