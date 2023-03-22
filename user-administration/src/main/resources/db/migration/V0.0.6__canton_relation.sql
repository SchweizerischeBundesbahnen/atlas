alter table business_organisation_user_permission
    rename to permission_restriction;


alter table permission_restriction
    rename column sboid to restriction;

alter table permission_restriction
    add column id bigint;

CREATE SEQUENCE permission_restriction_seq START WITH 1000 INCREMENT BY 1;

alter table permission_restriction
    add column type varchar(50);
update permission_restriction
set type = 'BUSINESS_ORGANISATION',
    id=nextval('permission_restriction_seq');
alter table permission_restriction
    alter column type set not null;
alter table permission_restriction
    alter column id set not null;

alter table permission_restriction
    add primary key (id);
