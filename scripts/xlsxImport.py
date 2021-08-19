# pip install wheel
# pip install pandas
# pip install openpyxl

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


# Read XLS File with Columns -> Linennr. renamed
xlsData = pandas.read_excel(r'data.xlsx')
data = pandas.DataFrame(xlsData,
												columns=['SLNID',
																 'V32 (Feldnummernverwealtung)',
																 'Element Beschreibung',
																 'Liniennr',
																 'Element gültig bis',
																 'Anmerkung'])

# Falls Linienr Leer, nicht importieren
data = data[data['Liniennr'].notnull()]

result_sql_file = open("../src/main/resources/db/scripts/initial_data.sql", mode="w", encoding="UTF-8")

fpnfid = 100000
for index, row in data.iterrows():
	name = row[1] if isinstance(row[1], str) else row[2]
	name = cleanhtml(name).replace("'", "''")

	result_sql_file.write(
			"INSERT INTO timetable_field_number_version "
			"(id, ttfnid, name, number, swiss_timetable_field_number, creation_date, creator, edition_date, editor, valid_from, valid_to, comment, name_compact, status) "
			"VALUES "
			"(nextval('timetable_field_number_version_seq'), 'ch:1:fpfnid:{}', '{}', '{}', '{}', current_timestamp, 'xlsx', current_timestamp, 'xlsx', '2020-12-12', '{}', '{}', null, 'ACTIVE');"
				.format(
					fpnfid,
					name,  # name
					row[3],  # number
					row[0],  # swiss_timetable_field_number
					reformat_date(row[4]),  # valid_to
					row[5]  # comment
			).replace("'nan'", "null"))
	result_sql_file.write("\n")
	result_sql_file.write(
			"INSERT INTO timetable_field_line_relation (id, slnid, timetable_field_version_id) "
			"VALUES (nextval('timetable_field_line_relation_seq'), 'ch:1:slnid:{}', currval('timetable_field_number_version_seq'));".format(
					fpnfid))
	result_sql_file.write("\n\n")

	fpnfid += 1

result_sql_file.close()
