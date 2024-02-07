create table allocated_sloid
(
    sloid     varchar(128) primary key,
    confirmed boolean not null default false
);

create table available_service_point_sloid
(
    sloid   varchar(128),
    country varchar(50),
    used    boolean not null default false,
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
SELECT ('ch:1:sloid:' || row_number() over (order by a.n)) as sloid, 'GERMANY_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n)) as sloid, 'AUSTRIA_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n)) as sloid, 'ITALY_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;

insert into available_service_point_sloid (sloid, country)
WITH empty_rows AS (
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL SELECT 1 AS n UNION ALL
    SELECT 1 AS n UNION ALL SELECT 1 AS n
)
SELECT ('ch:1:sloid:' || row_number() over (order by a.n)) as sloid, 'FRANCE_BUS' as country
FROM empty_rows as a, empty_rows as b, empty_rows as c, empty_rows as d, empty_rows as e limit 99999;
