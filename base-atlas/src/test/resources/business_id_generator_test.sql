CREATE SEQUENCE sboid_seq START WITH 1000000 INCREMENT BY 1;
create table id_generator_entity
(
    id bigint not null primary key,
    sboid               varchar(50)
);
