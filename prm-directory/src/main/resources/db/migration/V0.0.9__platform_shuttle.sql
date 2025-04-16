ALTER TABLE platform_version
    ADD COLUMN shuttle VARCHAR(50) default 'NO';

update platform_version
set shuttle = 'YES'
where lower(additional_information) like '%shuttle%'
   or lower(additional_information) like '%navette%';

update platform_version
set shuttle = 'YES'
where lower(advice_access_info) like '%shuttle%'
   or lower(advice_access_info) like '%navette%';
