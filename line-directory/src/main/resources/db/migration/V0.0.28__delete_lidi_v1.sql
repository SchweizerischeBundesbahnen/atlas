update line_version
set swiss_line_number=null,
    concession_type=null
where line_type != 'ORDERLY'
  and (swiss_line_number is not null or concession_type is not null);

-- LineVersion
alter table line_version drop column payment_type;
alter table line_version drop column alternative_name;
alter table line_version drop column combination_name;

alter table line_version drop column color_back_rgb;
alter table line_version drop column color_font_rgb;
alter table line_version drop column color_back_cmyk;
alter table line_version drop column color_font_cmyk;

alter table line_version drop column icon;

-- LineVersionSnapshot
alter table line_version_snapshot drop column payment_type;

-- SublineVersion
alter table subline_version drop column number;
alter table subline_version drop column payment_type;
