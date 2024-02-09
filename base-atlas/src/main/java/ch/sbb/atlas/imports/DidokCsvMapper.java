package ch.sbb.atlas.imports;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DidokCsvMapper {

  public static final CsvMapper CSV_MAPPER = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
  public static final CsvSchema CSV_SCHEMA = CsvSchema.emptySchema()
      .withHeader()
      .withColumnSeparator(';')
      .withAllowComments(true)
      .withEscapeChar('\\');

}
