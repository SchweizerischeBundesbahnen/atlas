# pip install wheel
# pip install pandas
# pip install openpyxl

import random
import re
from datetime import datetime

import pandas


# https://stackoverflow.com/questions/9662346/python-code-to-remove-html-tags-from-a-string
def cleanhtml(raw_html):
  cleanr = re.compile('<.*?>|&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-f]{1,6});')
  cleantext = re.sub(cleanr, '', raw_html)
  return cleantext


def reformat_date(date):
  try:
    date_to_format = date
    if (not isinstance(date, datetime)):
      date_to_format = datetime.strptime(date, '%d.%m.%Y')

    return date_to_format.strftime("%Y-%m-%d")
  except ValueError:
    return 'null'
  except TypeError:
    return '2099-12-31'


def random_rgb_color():
  r = lambda: random.randint(0, 255)
  return '#%02X%02X%02X' % (r(), r(), r())

def random_cymk_color():
  r = lambda: random.randint(0, 100)
  return '%d,%d,%d,%d' % (r(), r(), r(), r())


# Read XLS File with Columns -> Linennr. renamed
xlsData = pandas.read_excel(r'data.xlsx')
data = pandas.DataFrame(xlsData,
                        columns=['Element gültig',  # A
                                 'nTUV',  # B
                                 'SLNID',  # C
                                 'Liniennr',  # D
                                 'Element Beschreibung',  # I
                                 'Element Kommentar',  # J
                                 'Element gültig bis',  # K
                                 ])

# Wenn Spalte A nicht mit "Ja" ausgefüllt ist, ignorieren
data = data[data['Element gültig'].str.contains('Ja', na=False)]

# Wenn "Fpl-feld" in der Spalte B (nTUV) steht, wird der Eintrag nicht importiert
data = data[data['nTUV'].str.contains('Fpl-feld', na=True)]

result_sql_file = open("../src/main/resources/db/scripts/initial_data.sql",
                       mode="w", encoding="UTF-8")

payment_types = ['INTERNATIONAL', 'REGIONAL', 'REGIONALWITHOUT', 'LOCAL',
                 'OTHER', 'NONE']
subline_types = ['TECHNICAL', 'COMPENSATION']

slnid = 100000
for index, row in data.iterrows():

  description = str(row[4])  # I
  if description != 'nan':
    description = cleanhtml(description).replace("'", "''")

  # Wenn Spalte C ein Doppelpunkt enthält, ist es eine Teillinie
  if ':' in str(row[2]):
    result_sql_file.write(
        "INSERT INTO subline_version "
        "(id, swiss_subline_number, swiss_line_number, type, status, slnid, description, number, long_name, payment_type, valid_from, valid_to, business_organisation, creation_date, creator, edition_date, editor) "
        "VALUES "
        "(nextval('subline_version_seq'), '{}', '{}', '{}', 'ACTIVE', 'ch:1:slnid:{}', 'lorem ipsum Teillinie', '{}', '{}', '{}', '2020-12-12', '{}', 'ATLAS Transportation surrogate', current_timestamp, 'xlsx', current_timestamp, 'xlsx');"
          .format(
            row[2],
            str(row[2]).split(':', 1)[0],
            random.choice(subline_types),
            slnid,
            row[3],  # short_name
            description,  # long_name
            random.choice(payment_types),
            reformat_date(row[6]),  # Element gültig bis
        ).replace("'nan'", "null"))
    result_sql_file.write("\n")
  else:
    spalte_d = str(row[3])
    result_sql_file.write(
        "INSERT INTO line_version "
        "(id, status, type, slnid, payment_type, number, alternative_name, combination_name, long_name, color_font_rgb, color_back_rgb, color_font_cmyk, color_back_cmyk, "
        "icon, description, valid_from, valid_to, business_organisation, comment, swiss_line_number, creation_date, creator, edition_date, editor) "
        "VALUES "
        "(nextval('line_version_seq'), 'ACTIVE', 'ORDERLY', 'ch:1:slnid:{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', "
        "'https://en.wikipedia.org/wiki/File:Icon_train.svg', 'Lorem Ipsum Linie', '2020-12-12', '{}', 'SBB surrogate', '{}', '{}', current_timestamp, 'xlsx', current_timestamp, 'xlsx');"
          .format(
            slnid,
            random.choice(payment_types),
            spalte_d,  # SLNID
            spalte_d + ' alt',  # SLNID
            spalte_d + ' comb',  # SLNID
            description,  # Element Beschreibung
            random_rgb_color(),
            random_rgb_color(),
            random_cymk_color(),
            random_cymk_color(),
            reformat_date(row[6]),  # Element gültig bis
            str(row[5]).replace("'", "''"),  # Element Kommentar
            row[2]  # Liniennr
        ).replace("'nan'", "null"))
    result_sql_file.write("\n")

  slnid += 1

result_sql_file.close()
