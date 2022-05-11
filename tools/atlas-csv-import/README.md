# ATLAS CSV Import

<!-- toc -->

- [Run script](#run-script)
  * [Arguments](#arguments)
- [Semicolon escaping in csv file](#semicolon-escaping-in-csv-file)
- [Date format](#date-format)
- [Csv example files](#csv-example-files)

<!-- tocstop -->

## Run script
1. `npm install`
2. `npm start -- --token <token> --url <url> --csv <csvFilePath>` (You have to pass all three arguments but the order ist not relevant)

### Arguments
- **token**: Valid OAuth-Token to authenticate on the API. This token is added to the Authorization-Header
in the request as follows: `Bearer <token>`.
- **url**: The url from the api-endpoint to send the POST-request (insert new entity) to.
For example `http://localhost:8082/v1/lines/versions`
- **csv**: The local path to the csv-file from which the data should be read. (Example: `csv\line.csv`)

## Semicolon escaping in csv file
When they are semicolons(`;`) in the csv-values (not used as delimiters), they need to be replaced, because instead they
are read as delimiters. You have to replace the semicolon(`;`) with `$$` in the csv file. After the conversion
to json, the script replaces `$$` back with a semicolon(`;`).

## Date format
The dateformat for the fields: **validTo** and **validFrom** have to be like this `2021-01-25` to be valid for the api.
But the script also formats dates like this `25.01.2021` to the requested format.

## Csv example files
In the `csv/` folder are 3 csv-files with example data and the expected structure for every Entity located.
