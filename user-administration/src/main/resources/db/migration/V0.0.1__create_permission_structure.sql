CREATE SEQUENCE user_permission_seq START WITH 1000 INCREMENT BY 1;

create table user_permission (
    id bigint primary key,
    role varchar(20) not null,
    application varchar(20) not null,
    sbb_user_id varchar(7) not null,
    constraint user_application_unique unique (application, sbb_user_id)
);

create table business_organisation_user_permission (
    sboid varchar(32) not null,
    user_permission_id bigint not null,
    constraint fk_user_permission_id foreign key (user_permission_id) references user_permission (id)
);
