# pip install wheel
# pip install pandas
# pip install openpyxl

import pandas

business_types = {
  "30": "STREET",
  "32": "STREET_WITHOUT_TRAFFIC",
  "10": "RAILROAD",
  "11": "RAILROAD_UIC",
  "12": "TRAIN_WITHOUT_TRAFFIC",
  "20": "SHIP",
  "22": "SHIP_WITHOUT_TRAFFIC",
  "45": "AIR",
  "50": "LEISURE_ACTIVITIES",
  "51": "TARIFF_ASSOCIATION",
  "52": "FAIR",
  "60": "TRAVEL_AGENCY_ORGANISATION",
  "70": "CUSTOMER_INFORMATION",
  "80": "SUBSIDIARY",
  "95": "INTERNAL_BILLING_PURPOSES",
  "99": "UNKNOWN",
}


def insertTypesToTable(list_of_types, result_sql_file):
  for business_type in list_of_types:
    result_sql_file.write(
        "INSERT INTO business_organisation_version_business_types VALUES (currval('business_organisation_version_seq'), '{}');"
          .format(business_types.get(business_type)))
    result_sql_file.write("\n")

def getDate(input_date):
  dateSplit = input_date.split(".")
  if len(dateSplit) == 3:
    return f'{dateSplit[2]}-{dateSplit[1]}-{dateSplit[0]}'
  return input_date


def main():
  print("Reading GO Data")
  go_data = pandas.read_csv(r'220803_ATLAS_EXP_GOS.csv', sep=';', header=0)

  result_sql_file = open(
      "../src/main/resources/db/scripts/initial_go_data.sql",
      mode="w", encoding="UTF-8")
  print("Writing GO Data to file")

  # Appostrophe behandeln für SQL
  go_data = go_data.replace("'", "''", regex=True)

  for index, row in go_data.iterrows():
    result_sql_file.write(
        "INSERT INTO business_organisation_version "
        " (id, sboid, "
        "abbreviation_de, abbreviation_fr, abbreviation_it, abbreviation_en, "
        "description_de, description_fr, description_it, description_en, "
        "organisation_number, contact_enterprise_email, status,"
        " valid_from, valid_to, creation_date, creator, edition_date, editor) "
        "VALUES "
        "(nextval('business_organisation_version_seq'), '{}', "
        "'{}', '{}', '{}', '{}', "
        "'{}', '{}', '{}', '{}', "
        "'{}', {}, 'ACTIVE', "
        "'{}', '{}', current_timestamp, 'didok', current_timestamp, 'didok');"
          .format(row.SBOID,
                  row.AbbreviationDE, row.AbbreviationFR, row.AbbreviationIT,
                  row.AbbreviationEN,
                  row.DescriptionDE, row.DescriptionFR, row.DescriptionIT,
                  row.DescriptionEN,
                  row.OrganisationNumber,
                  "null" if isinstance(row.ContactEnterpriseMail,
                                       float) else "'" + row.ContactEnterpriseMail + "'",
                  getDate(row.validFrom), getDate(row.validTo)
                  ))
    result_sql_file.write("\n")

    raw_business_types = str(row.types)

    # If types contain a type
    if raw_business_types != "nan":
      list_of_types = raw_business_types.split("|")
      insertTypesToTable(list_of_types, result_sql_file)

    result_sql_file.write("\n")

  result_sql_file.close()


if __name__ == '__main__':
  main()
