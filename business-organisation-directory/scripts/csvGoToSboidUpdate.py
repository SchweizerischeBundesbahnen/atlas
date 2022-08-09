# pip install wheel
# pip install pandas
# pip install openpyxl

import pandas

def main():
  print("Reading GO Data")
  go_data = pandas.read_csv(r'220803_ATLAS_EXP_GOS.csv', sep=';', header=0)

  result_sql_file = open(
      "../src/main/resources/db/scripts/update_go_to_sboid.sql",
      mode="w", encoding="UTF-8")
  print("Writing GO Data to file")

  # Appostrophe behandeln für SQL
  go_data = go_data.replace("'", "''", regex=True)

  for index, row in go_data.iterrows():
    result_sql_file.write(
        f"update line_version set business_organisation='{row.SBOID}' where business_organisation='{row.OrganisationNumber} - {row.AbbreviationDE}';")
    result_sql_file.write("\n")


  result_sql_file.close()


if __name__ == '__main__':
  main()
