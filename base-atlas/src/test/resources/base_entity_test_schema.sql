create table base_entity
(
    id bigint not null primary key,
    creation_date         timestamp        not null,
    creator               varchar(50),
    edition_date          timestamp        not null,
    editor                varchar(50),
    version               bigint default 0 not null
);
