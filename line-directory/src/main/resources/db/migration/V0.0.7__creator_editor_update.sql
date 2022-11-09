update line_version set editor = 'fxatlsy' where lower(editor) in ('u236171', 'didok');
update line_version set editor = 'fxatlka' where lower(editor) in ('kafka_system_user');
update subline_version set editor = 'fxatlsy' where lower(editor) in ('u236171', 'didok');
update timetable_field_number_version set editor = 'fxatlsy' where lower(editor) in ('u236171', 'didok');

update line_version set creator = 'fxatlsy' where lower(creator) in ('u236171', 'didok');
update line_version set creator = 'fxatlka' where lower(creator) in ('kafka_system_user');
update subline_version set creator = 'fxatlsy' where lower(creator) in ('u236171', 'didok');
update timetable_field_number_version set creator = 'fxatlsy' where lower(creator) in ('u236171', 'didok');