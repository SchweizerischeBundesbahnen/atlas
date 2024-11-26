ALTER TABLE line_version
    ALTER COLUMN color_back_cmyk DROP NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN color_font_cmyk DROP NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN color_back_rgb DROP NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN color_font_rgb DROP NOT NULL;

ALTER TABLE line_version_snapshot
    ALTER COLUMN payment_type DROP NOT NULL;
ALTER TABLE line_version_snapshot
    DROP COLUMN color_back_cmyk;
ALTER TABLE line_version_snapshot
    DROP COLUMN color_font_cmyk;
ALTER TABLE line_version_snapshot
    DROP COLUMN color_back_rgb;
ALTER TABLE line_version_snapshot
    DROP COLUMN color_font_rgb;
ALTER TABLE line_version_snapshot
    DROP COLUMN icon;
ALTER TABLE line_version_snapshot
    DROP COLUMN alternative_name;
ALTER TABLE line_version_snapshot
    DROP COLUMN combination_name;