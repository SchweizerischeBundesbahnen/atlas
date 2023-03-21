create table beneficiary
(
    id             bigint primary key,
    type           varchar(20) not null,
    identification varchar(20) not null,
    alias          varchar(20),
    comment        timestamp
);
CREATE SEQUENCE beneficiary_seq START WITH 1000 INCREMENT BY 1;

-- ADD Foreign Key
alter table user_permission
    add column beneficiary_id bigint;
alter table user_permission
    add foreign key (beneficiary_id) REFERENCES beneficiary (id);

-- Migrate existing data
INSERT into beneficiary (id, type, identification)
select nextval('beneficiary_seq'), 'USER', sbb_user_id
from (select distinct sbb_user_id from user_permission) as user_ids;

UPDATE user_permission
set beneficiary_id = beneficiary_ids.id
from (select id, identification from beneficiary) as beneficiary_ids
where sbb_user_id = beneficiary_ids.identification;

-- Cleanup
alter table user_permission
    alter column beneficiary_id set not null;

alter table user_permission
    drop column sbb_user_id;
