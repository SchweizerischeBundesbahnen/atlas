package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class AtlasCsvReader {

  public static List<ServicePointAtlasCsvModel> parseAtlasServicePoints(InputStream inputStream)
      throws IOException {
    MappingIterator<ServicePointAtlasCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointAtlasCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<ServicePointAtlasCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

  public static List<TrafficPointAtlasCsvModel> parseAtlasTraffics(InputStream inputStream)
      throws IOException {
    MappingIterator<TrafficPointAtlasCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        TrafficPointAtlasCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<TrafficPointAtlasCsvModel> trafficPointAtlasCsvModels = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      trafficPointAtlasCsvModels.add(mappingIterator.next());
    }
    return trafficPointAtlasCsvModels;
  }

  public static LocalDate dateFromString(String string) {
    return LocalDate.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
