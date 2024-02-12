-- Insert Categories
insert into service_point_version_categories (service_point_version_id, categories)
values (1228, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1394, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1481, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1519, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1712, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1746, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1763, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1785, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1794, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1798, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1841, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1845, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1897, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1982, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1981, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1987, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (2055, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (2068, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1228, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1394, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1481, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1519, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1712, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1746, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1763, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1785, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1794, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1798, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1841, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1845, 'POINT_OF_SALE');
insert into service_point_version_categories (service_point_version_id, categories)
values (1897, 'POINT_OF_SALE');

-- Insert ServicePointGeolocation
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1000, 'LV95', 2628500.00000000000, 1263200.00000000000, 329.00, 'RUSSIA', 'AARGAU', 'Rheinfelden', 1909, 4253,
        'Magden', 'Magden', '2017-11-09 11:53:05.000000', 'GSU_DIDOK', '2022-02-23 17:53:50.000000', 'GSU_DIDOK', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1001, 'WGS84', 11.41876400000, 47.08358500000, 0.00, 'AUSTRIA_BUS', null, null, null, null, null, null,
        '2022-09-10 17:30:56.000000', 'fs45117', '2022-09-10 17:30:56.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1002, 'WGS84', 11.40638500000, 47.07800000000, 0.00, 'AUSTRIA_BUS', null, null, null, null, null, null,
        '2022-09-10 17:29:29.000000', 'fs45117', '2022-09-10 17:29:29.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1003, 'LV95', 2628250.00000000000, 1263900.00000000000, 326.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4253,
        'Magden', 'Magden', '2017-11-09 11:53:05.000000', 'GSU_DIDOK', '2022-02-23 18:04:23.000000', 'GSU_DIDOK', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1004, 'LV95', 2625254.00000000000, 1263658.00000000000, 360.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4257,
        'Olsberg', 'Olsberg', '2021-03-19 09:08:46.000000', 'fs45117', '2022-02-23 20:03:21.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1005, 'LV95', 2625254.00000000000, 1263658.00000000000, 360.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4257,
        'Olsberg', 'Olsberg', '2022-07-29 11:10:23.000000', 'fs45117', '2022-07-29 11:10:23.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1006, 'LV03', 625254.00000000000, 263658.00000000000, 360.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4257,
        'Olsberg', 'Olsberg', '2017-11-09 11:53:05.000000', 'GSU_DIDOK', '2022-07-29 11:10:23.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1007, 'WGS84', 11.45239700000, 47.08845400000, 0.00, 'AUSTRIA_BUS', null, null, null, null, null, null,
        '2022-09-10 17:25:07.000000', 'fs45117', '2022-09-10 17:25:07.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1008, 'WGS84', 12.75284300000, 46.84871300000, 0.00, 'AUSTRIA_BUS', null, null, null, null, null, null,
        '2022-09-10 17:03:15.000000', 'fs45117', '2022-09-10 17:03:15.000000', 'fs45117', 0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1009, 'LV95', 2627323.00000000000, 1266834.00000000000, 296.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4258,
        'Rheinfelden', 'Rheinfelden', '2021-03-19 09:09:39.000000', 'fs45117', '2021-03-19 09:09:39.000000', 'fs45117',
        0);
insert into service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                               swiss_district_name, swiss_district_number, swiss_municipality_number,
                                               swiss_municipality_name, swiss_locality_name, creation_date, creator,
                                               edition_date,
                                               editor, version)
values (1010, 'LV95', 2627323.00000000000, 1266834.00000000000, 296.00, 'SWITZERLAND', 'AARGAU', 'Rheinfelden', 1909,
        4258,
        'Rheinfelden', 'Rheinfelden', '2022-07-29 11:10:29.000000', 'fs45117', '2022-07-29 11:10:29.000000', 'fs45117',
        0);

-- Insert ServicePointVersion
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1000, 1000, 8572241, 'ch:1:sloid:72241', 72241, 'SWITZERLAND', null, 'Magden, Obrist', null, 'HISTORICAL',
        null,
        'ch:1:sboid:100602', null, null, 'VALIDATED', null, false, '1993-02-01',
        '2020-12-12', '2017-11-09 11:53:05.000000', 'GSU_DIDOK', '2022-02-23 17:53:50.000000', 'GSU_DIDOK', 0, false,
        true, true,
        null, null);
insert into service_point_fot_comment (service_point_number, fot_comment, creation_date, creator, edition_date, editor,
                                       version)
values (8572241, '(Bus); ohne; Fahrplandaten 2016/2018', '2017-11-09 11:53:05.000000', 'GSU_DIDOK',
        '2022-02-23 17:53:50.000000',
        'GSU_DIDOK', 0);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1001, null, 9411114, null, 11114, 'PORTUGAL', null, 'Ferradosa', null, 'IN_OPERATION', null,
        'ch:1:sboid:100885', null,
        null, 'VALIDATED', null, false, '2001-01-01', '2010-12-11', '2018-02-15 23:28:53.000000', 'fs45117',
        '2018-03-08 18:21:49.000000', 'fs45117', 0, false, true, true, null, null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1002, null, 9411114, null, 11114, 'PORTUGAL', null, 'Ferradosa', null, 'IN_OPERATION', null,
        'ch:1:sboid:100885', null,
        null, 'VALIDATED', null, false, '2010-12-12', '2020-08-31', '2020-09-03 14:58:44.000000', 'fs45117',
        '2020-09-03 14:58:44.000000', 'fs45117', 1, false, true, true, null, null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1003, null, 9411114, null, 11114, 'PORTUGAL', null, 'Ferradosa', null, 'IN_OPERATION', null,
        'ch:1:sboid:100885', null,
        null, 'VALIDATED', null, false, '2020-09-01', '2020-12-12', '2017-11-09 11:53:05.000000', 'GSU_DIDOK',
        '2020-09-03 15:29:14.000000', 'fs45117', 2, false, true, true, null, null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1004, null, 1101019, null, 1019, 'GERMANY_BUS', null, 'München Hbf Nord II', null, 'HISTORICAL', null,
        'ch:1:sboid:101698', null, null, 'VALIDATED', null, false, '2015-04-09', '2021-05-05',
        '2017-11-09 11:53:05.000000',
        'GSU_DIDOK', '2021-08-12 22:35:38.000000', 'GSU_DIDOK', 0, false, false, false, null, null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1005, 1001, 1205887, null, 5887, 'AUSTRIA_BUS', null, 'Trins, Wienerhof', null, 'IN_OPERATION', null,
        'ch:1:sboid:101257', null, 'UNKNOWN', 'VALIDATED', null, false, '2022-09-01', '2022-12-31',
        '2022-09-10 17:30:56.000000', 'fs45117', '2022-09-10 17:30:56.000000', 'fs45117', 0, false, true, true, null,
        null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1006, null, 1101027, null, 1027, 'GERMANY_BUS', null, 'Bad Camberg Mecklenburgerstr.', null, 'HISTORICAL',
        null,
        'ch:1:sboid:101698', null, null, 'VALIDATED', null, false, '2015-04-10', '2021-05-05',
        '2017-11-09 11:53:05.000000',
        'GSU_DIDOK', '2021-08-12 22:35:38.000000', 'GSU_DIDOK', 0, false, false, false, null, null);
insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country,
                                   designation_long,
                                   designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                   business_organisation, operating_point_type, stop_point_type, status,
                                   operating_point_kilometer_master, operating_point_route_network, valid_from,
                                   valid_to,
                                   creation_date, creator, edition_date, editor, version, freight_service_point,
                                   operating_point,
                                   operating_point_with_timetable, operating_point_technical_timetable_type,
                                   operating_point_traffic_point_type)
values (1007, 1002, 1205886, null, 5886, 'AUSTRIA_BUS', null, 'Trins, Waldfestplatz', null, 'IN_OPERATION', null,
        'ch:1:sboid:101257', null, 'UNKNOWN', 'VALIDATED', null, false, '2022-09-01', '2022-12-31',
        '2022-09-10 17:29:29.000000', 'fs45117', '2022-09-10 17:29:29.000000', 'fs45117', 0, false, true, true, null,
        null);

-- Insert SharedBusinessOrganisationVersion
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2716, 'ch:1:sboid:101257', 'TSDA', 'TSDA', 'TSDA', 'TSDA', 'Télésiège Les Dappes - La Dôle',
        'Télésiège Les Dappes - La Dôle', 'Télésiège Les Dappes - La Dôle', 'Télésiège Les Dappes - La Dôle', 3065,
        'VALIDATED',
        '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2717, 'ch:1:sboid:101698', 'BDGAG-ssl', 'BDGAG-ssl', 'BDGAG-ssl', 'BDGAG-ssl',
        'Bergbahnen Destination Gstaad AG',
        'Bergbahnen Destination Gstaad AG', 'Bergbahnen Destination Gstaad AG', 'Bergbahnen Destination Gstaad AG',
        3066,
        'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2718, 'ch:1:sboid:100885', 'BDGAG-slg', 'BDGAG-slg', 'BDGAG-slg', 'BDGAG-slg',
        'Bergbahnen Destination Gstaad AG',
        'Bergbahnen Destination Gstaad AG', 'Bergbahnen Destination Gstaad AG', 'Bergbahnen Destination Gstaad AG',
        3067,
        'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2719, 'ch:1:sboid:100602', 'SAS-Code', 'SAS-Code', 'SAS-Code', 'SAS-Code',
        'Reserviert für SAS (technischer Code)',
        'Reserviert für SAS (technischer Code)', 'Reserviert für SAS (technischer Code)',
        'Reserviert für SAS (technischer Code)',
        999, 'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2720, 'ch:1:sboid:100799', 'Pool', 'Pool', 'Pool', 'Pool', 'Internationaler Schlafwagenpool',
        'Pool international des wagons-lits', 'Internationaler Schlafwagenpool', 'Internationaler Schlafwagenpool',
        1004,
        'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2721, 'ch:1:sboid:100800', 'St.L', 'St.L', 'St.L', 'St.L', 'Stena Line (Ferry-boat)', 'Stena Line (Ferry-boat)',
        'Stena Line (Ferry-boat)', 'Stena Line (Ferry-boat)', 1006, 'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2722, 'ch:1:sboid:100801', 'IC', 'IC', 'IC', 'IC', 'Intercontainer', 'Intercontainer', 'Intercontainer',
        'Intercontainer',
        1007, 'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2723, 'ch:1:sboid:101116', 'FBG', 'FBG', 'FBG', 'FBG', 'Fähre Beckenried-Gersau', 'Fähre Beckenried-Gersau',
        'Fähre Beckenried-Gersau', 'Fähre Beckenried-Gersau', 3190, 'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (2724, 'ch:1:sboid:101117', 'WG-wg', 'WG-wg', 'WG-wg', 'WG-wg', 'Wasserngrat 2000 AG', 'Wasserngrat 2000 AG',
        'Wasserngrat 2000 AG', 'Wasserngrat 2000 AG', 3191, 'VALIDATED', '1900-01-01', '2099-12-31');
insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr,
                                                  abbreviation_it, abbreviation_en, description_de,
                                                  description_fr, description_it, description_en,
                                                  organisation_number, status, valid_from, valid_to)
values (5246, 'ch:1:sboid:1102313', 'test1026', 'tIdd', 'TIff', 'STIf', 'MIMO2 - 3883', 'Verkehrsbetriebe STI AG',
        'Verkehrsbetriebe STI AG', 'Verkehrsbetriebe STI AG', 3883, 'VALIDATED', '2023-01-01', '2023-12-31');

-- Insert Traffic Point Element Geolocation Versions

insert into traffic_point_element_version_geolocation (id, spatial_reference, east, north,
                                                       height, creation_date, creator,
                                                       edition_date, editor, version)
values (1000, 'LV03', 681821.00000000000, 225738.00000000000, 423.000, '2022-03-03 07:56:42.000000', 'fs45117',
        '2022-05-03 11:50:46.000000', 'e536178', 0);

insert into traffic_point_element_version_geolocation (id, spatial_reference, east, north,
                                                       height, creation_date, creator,
                                                       edition_date, editor, version)
values (1001, 'LV03', 736585.00000000000, 244767.00000000000, 897.000, '2023-04-27 06:26:01.000000', 'fs45117',
        '2023-04-27 06:27:26.000000', 'fs45117', 0);
insert into traffic_point_element_version_geolocation (id, spatial_reference, east, north,
                                                       height, creation_date, creator,
                                                       edition_date, editor, version)
values (1003, 'LV95', 2613930.50900000000, 1192211.42900000000, 663.200, '2020-07-03 08:12:08.000000', 'fs45117',
        '2020-07-03 08:22:43.000000', 'fs45117', 0);
insert into traffic_point_element_version_geolocation (id, spatial_reference, east, north,
                                                       height, creation_date, creator,
                                                       edition_date, editor, version)
values (1002, 'LV03', 586309.00000000000, 198845.00000000000, 615.000, '2023-03-03 08:11:46.000000', 'ue02112',
        '2023-04-27 10:27:18.000000', 'fs45117', 0);

-- Insert Traffic Point Element Versions
insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
                                           designation_operational, traffic_point_element_type,
                                           length, boarding_area_height, compass_direction,
                                           service_point_number, valid_from, valid_to,
                                           traffic_point_geolocation_id, creation_date, creator,
                                           edition_date, editor, version)
values (1000, 'ch:1:sloid:77559:0:2', null, null, '2', 'BOARDING_PLATFORM', 18.000, 2.00, null, 1205887, '2022-03-01', '2099-12-31',
        1000, '2022-03-03 07:56:42.000000', 'fs45117', '2022-05-03 11:50:46.000000', 'e536178', 0);
insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
                                           designation_operational, traffic_point_element_type,
                                           length, boarding_area_height, compass_direction,
                                           service_point_number, valid_from, valid_to,
                                           traffic_point_geolocation_id, creation_date, creator,
                                           edition_date, editor, version)
values (1001, 'ch:1:sloid:88253:0:1', null, null, '1', 'BOARDING_PLATFORM', null, null, 57.00, 9411114, '2020-07-03', '2099-12-31',
        1001, '2020-07-03 08:12:08.000000', 'fs45117', '2020-07-03 08:22:43.000000', 'fs45117', 0);
insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
                                           designation_operational, traffic_point_element_type,
                                           length, boarding_area_height, compass_direction,
                                           service_point_number, valid_from, valid_to,
                                           traffic_point_geolocation_id, creation_date, creator,
                                           edition_date, editor, version)
values (1002, 'ch:1:sloid:6602:0:7110', null, null, 'EINSIEG1', 'BOARDING_PLATFORM', null, null, null, 8506602, '2021-12-12',
        '2023-04-25', 1002, '2023-04-27 06:26:01.000000', 'fs45117', '2023-04-27 06:27:26.000000', 'fs45117', 0);
insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
                                           designation_operational, traffic_point_element_type,
                                           length, boarding_area_height, compass_direction,
                                           service_point_number, valid_from, valid_to,
                                           traffic_point_geolocation_id, creation_date, creator,
                                           edition_date, editor, version)
values (1003, 'ch:1:sloid:71449:0:523431', null, null, '177MAUS1', 'BOARDING_PLATFORM', null, null, null, 1205887, '2021-12-12',
        '2023-03-26', 1003, '2023-03-03 08:11:46.000000', 'ue02112', '2023-04-27 10:27:18.000000', 'fs45117', 0);
insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
                                           designation_operational, traffic_point_element_type,
                                           length, boarding_area_height, compass_direction,
                                           service_point_number, valid_from, valid_to,
                                           traffic_point_geolocation_id, creation_date, creator,
                                           edition_date, editor, version)
values (1004, 'ch:1:sloid:3020:1', null, 'Perron 1', null, 'BOARDING_AREA', null, null, null, 1205886, '2020-05-07', '2099-12-31',
        null, '2020-05-18 12:43:34.000000', 'fs45117', '2020-05-18 12:43:34.000000', 'fs45117', 0);

-- loading points
insert into loading_point_version (id,
                                   number,
                                   designation,
                                   designation_long,
                                   connection_point,
                                   service_point_number,
                                   valid_from,
                                   valid_to,
                                   creation_date,
                                   creator,
                                   edition_date,
                                   editor,
                                   version)
values (1000, 50, 'Ladestelle 1', 'Ladestelle Lang', false, 8509111, '1993-01-01', '2020-05-05', '2020-05-18 12:43:34.000000',
        'fs45117', '2020-05-18 12:43:34.000000', 'fs45117', 0);

insert into loading_point_version (id,
                                   number,
                                   designation,
                                   designation_long,
                                   connection_point,
                                   service_point_number,
                                   valid_from,
                                   valid_to,
                                   creation_date,
                                   creator,
                                   edition_date,
                                   editor,
                                   version)
values (1001, 60, 'Ladestelle 2', 'Ladestelle Lang', false, 8509222, '2020-01-01', '2099-12-31', '2020-05-18 12:43:34.000000',
        'fs45117', '2020-05-18 12:43:34.000000', 'fs45117', 0);

insert into loading_point_version (id,
                                   number,
                                   designation,
                                   designation_long,
                                   connection_point,
                                   service_point_number,
                                   valid_from,
                                   valid_to,
                                   creation_date,
                                   creator,
                                   edition_date,
                                   editor,
                                   version)
values (1002, 70, 'Ladestelle 3', 'Ladestelle Lang', true, 8509333, '2040-01-01', '2050-12-31', '2020-05-18 12:43:34.000000',
        'fs45117', '2020-05-18 12:43:34.000000', 'fs45117', 0);
