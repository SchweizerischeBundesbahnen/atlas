package ch.sbb.atlas.imports.bulk;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCsvReader {

  public static final CsvMapper CSV_MAPPER = new CsvMapper()
      .enable(Feature.EMPTY_STRING_AS_NULL)
      .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);

  public static final char CSV_COLUMN_SEPARATOR = ';';
  public static final CsvSchema CSV_SCHEMA = CsvSchema.emptySchema()
      .withHeader()
      .withColumnSeparator(CSV_COLUMN_SEPARATOR);

}