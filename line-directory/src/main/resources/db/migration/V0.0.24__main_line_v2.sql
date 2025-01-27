ALTER TABLE line_version
    ALTER COLUMN description set NOT NULL;
ALTER TABLE line_version
    ALTER COLUMN number set NOT NULL;

-- Orderly => concessionType mandatory => EK
update line_version
set concession_type = 'FEDERALLY_LICENSED_OR_APPROVED_LINE'
where line_type = 'ORDERLY'
  and concession_type is null;

-- !Orderly => swissLineNumber and concessionType have to be null
update line_version
set swiss_line_number = null,
    concession_type = null
where line_type != 'ORDERLY';