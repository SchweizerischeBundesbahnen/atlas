package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCsvReader {

  public static List<ServicePointVersionCsvModel> parseAtlasServicePoints(InputStream inputStream)
      throws IOException {
    MappingIterator<ServicePointVersionCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointVersionCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<ServicePointVersionCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

  public static LocalDateTime timestampFromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
  }

  public static LocalDate dateFromString(String string) {
    return LocalDate.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
