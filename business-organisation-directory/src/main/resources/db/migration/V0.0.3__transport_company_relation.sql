create table transport_company_relation
(
    id bigint primary key,
    transport_company_id bigint not null,
    sboid varchar(32) not null,
    valid_from date not null,
    valid_to date not null,
    creator varchar(50) not null ,
    creation_date timestamp not null,
    editor varchar(50) not null,
    edition_date timestamp not null,
    version bigint not null default 0,
    foreign key (transport_company_id) references transport_company (id)
);

create sequence transport_company_relation_seq start with 1000 increment by 1;
