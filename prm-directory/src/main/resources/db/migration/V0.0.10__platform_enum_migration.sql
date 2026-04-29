UPDATE platform_version
SET level_access_wheelchair = 'YES_WITH_STAFF_ASSISTANCE'
WHERE advice_access_info like '%Hilfestellung durch Fahrpersonal%'
   OR advice_access_info like '%le conducteur assiste la clientèle%'
   OR advice_access_info like '%Hilfestellung durch Buschauffeur%'
   OR advice_access_info like '%Hilfestellung durch den Lokführer%';
