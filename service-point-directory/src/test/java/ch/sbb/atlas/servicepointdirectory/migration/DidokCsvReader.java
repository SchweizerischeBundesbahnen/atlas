package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DidokCsvReader {

  public List<ServicePointDidokCsvModel> parseDidokServicePoints(InputStream inputStream)
      throws IOException {
    MappingIterator<ServicePointDidokCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointDidokCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<ServicePointDidokCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

  public List<TrafficPointDidokCsvModel> parseDidokTrafficPoints(InputStream inputStream)
      throws IOException {
    MappingIterator<TrafficPointDidokCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        TrafficPointDidokCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<TrafficPointDidokCsvModel> trafficPointElements = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      trafficPointElements.add(mappingIterator.next());
    }
    return trafficPointElements;
  }

}
