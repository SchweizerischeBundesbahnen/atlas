create table business_organisation_transport_company_link
(
    id integer unique not null primary key,
    transport_company_id smallint not null,
    sboid varchar(32) not null,
    valid_from date not null,
    valid_to date not null,
    creator varchar(50) not null ,
    creation_date timestamp not null,
    editor varchar(50) not null,
    edition_date timestamp not null,
    version BIGINT NOT NULL DEFAULT 0,
    foreign key (transport_company_id) references transport_company (id)
);

CREATE SEQUENCE business_organisation_transport_company_link_seq START WITH 1000 INCREMENT BY 1;
