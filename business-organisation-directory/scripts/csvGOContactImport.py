# pip install wheel
# pip install pandas
# pip install openpyxl
# pip install xlrd

import pandas


def main():
  print("Reading data")
  go_data = pandas.read_excel(r'220719_GO_export_full.xls', header=0)

  result_sql_file = open(
      "../src/main/resources/db/scripts/update_go_contact_data.sql",
      mode="w", encoding="UTF-8")
  print("Writing contact data to file")

  for index, row in go_data.iterrows():

    if row.KONTAKT_EMAIL.strip() not in ['test@test.ch', 'dummy@sbb.ch', '',
                                         None, 'Dummy_Mail']:
      sboid = 'ch:1:sboid:' + str(row.IDENTIFIKATION)

      result_sql_file.write(
        f"update business_organisation_version set contact_enterprise_email = '{row.KONTAKT_EMAIL}' where sboid='{sboid}' and contact_enterprise_email is null;")
      result_sql_file.write("\n")

  result_sql_file.close()


if __name__ == '__main__':
  main()
