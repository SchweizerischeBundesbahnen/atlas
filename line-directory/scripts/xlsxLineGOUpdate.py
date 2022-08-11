# pip install wheel
# pip install pandas
# pip install openpyxl

import random
import re
from datetime import datetime

import pandas




def main():
  print("Reading LiDi Data")
  xlsData = pandas.read_excel(r'GO Zuordnung zu Linien.xlsx')
  data = pandas.DataFrame(xlsData,
                          columns=['Column3',  # SLNID
                                   'GO-Fahrplanfeld',  # GO
                                            ])

  result_sql_file = open(
    "../src/main/resources/db/scripts/line_go_update_data.sql",
    mode="w", encoding="UTF-8")


  for index, row in data.iterrows():
    result_sql_file.write(
        f"update line_version set business_organisation = '{row[1]}' where slnid='{row[0]}';")
    result_sql_file.write("\n")
  result_sql_file.close()


if __name__ == '__main__':
  main()
