ALTER TABLE subline_version ALTER COLUMN swiss_subline_number DROP NOT NULL;

delete from coverage where slnid in (select slnid from subline_version where description is null);
delete from subline_version where description is null;

ALTER TABLE subline_version ALTER COLUMN description set not null;

update subline_version set concession_type = 'FEDERALLY_LICENSED_OR_APPROVED_LINE' where subline_type = 'CONCESSION';