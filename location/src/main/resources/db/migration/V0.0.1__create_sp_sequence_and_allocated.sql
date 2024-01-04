create sequence service_point_sloid_seq
    start with 1
    increment by 1;

create table service_point_sloid_allocated
(
    number integer primary key
);
