# pip install wheel
# pip install pandas
# pip install openpyxl

import pandas

def main():
  go_data = pandas.read_csv(r'business_organisation_version_prod.csv')

  result_sql_file = open(
      "../../line-directory/src/main/resources/db/scripts/update_line_go_to_sboid.sql",
      mode="w", encoding="UTF-8")

  for index, row in go_data.iterrows():
    result_sql_file.write(
        f"update line_version set business_organisation='{row.sboid}' where business_organisation like '{row.organisation_number} %';")
    result_sql_file.write("\n")

  result_sql_file.close()


if __name__ == '__main__':
  main()
