-- Blank (whitespace-only) first_name/last_name values were previously accepted by validation
-- (ATLAS person names must not be blank, null remains allowed).
UPDATE person
SET first_name = NULL
WHERE first_name IS NOT NULL
  AND trim(first_name) = '';

UPDATE person
SET last_name = NULL
WHERE last_name IS NOT NULL
  AND trim(last_name) = '';
