ALTER TABLE person add column default_examinant boolean not null default false;
update person set default_examinant = true where "function" = 'Verkehrsplanung' or "function" = 'Fachstelle atlas';