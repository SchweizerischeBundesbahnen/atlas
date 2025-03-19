update line_version
set concession_type = 'VARIANT_OF_A_LICENSED_LINE'
where concession_type = 'VARIANT_OF_A_FRANCHISED_LINE';

update line_version
set concession_type = 'FEDERAL_ZONE_CONCESSION'
where concession_type = 'FEDERAL_TERRITORIAL_CONCESSION';

update line_version
set concession_type = 'LINE_OF_A_ZONE_CONCESSION'
where concession_type = 'LINE_OF_A_TERRITORIAL_CONCESSION';

update line_version
set concession_type = 'RIGHT_FREE_LINE'
where concession_type = 'RACK_FREE_TRIPS';

update line_version
set concession_type = 'NOT_LICENSED_UNPUBLISHED_LINE'
where concession_type = 'RACK_FREE_UNPUBLISHED_LINE';

update subline_version
set concession_type = 'VARIANT_OF_A_LICENSED_LINE'
where concession_type = 'VARIANT_OF_A_FRANCHISED_LINE';

update subline_version
set concession_type = 'RIGHT_FREE_LINE'
where concession_type = 'RACK_FREE_TRIPS';

update subline_version
set concession_type = 'NOT_LICENSED_UNPUBLISHED_LINE'
where concession_type = 'RACK_FREE_UNPUBLISHED_LINE';