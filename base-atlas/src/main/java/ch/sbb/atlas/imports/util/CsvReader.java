package ch.sbb.atlas.imports.util;

import ch.sbb.atlas.api.AtlasApiConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CsvReader {

  public final String BASE_PATH = "/migration/";

  public <T> List<T> parseCsv(InputStream inputStream, Class<T> type) throws IOException {
    MappingIterator<T> mappingIterator = DidokCsvMapper.CSV_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readerFor(type).with(DidokCsvMapper.CSV_SCHEMA)
        .readValues(inputStream);
    final List<T> csvModels = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      csvModels.add(mappingIterator.next());
    }
    return csvModels;
  }

  public LocalDate dateFromString(String string) {
    return LocalDate.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }

}
