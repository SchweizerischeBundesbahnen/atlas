UPDATE line_version
set color_font_rgb = '#000000'
where color_font_rgb is null;
UPDATE line_version
set color_back_rgb = '#FFFFFF'
where color_back_rgb is null;

UPDATE line_version
set color_font_cmyk = '100,100,100,100'
where color_font_cmyk is null;
UPDATE line_version
set color_back_cmyk = '0,0,0,0'
where color_back_cmyk is null;

COMMIT;

ALTER TABLE line_version
    ALTER COLUMN color_font_rgb set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN color_back_rgb set NOT NULL;

ALTER TABLE line_version
    ALTER COLUMN color_font_cmyk set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN color_back_rgb set NOT NULL;

