create table canton_user_permission
(
    user_permission_id bigint      not null,
    swiss_cantons      varchar(50) not null,
    foreign key (user_permission_id) references user_permission (id)
);