package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrafficPointElementImportService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  public void importTrafficPointElements(List<TrafficPointElementCsvModel> csvModels) {
    List<TrafficPointElementVersion> trafficPointElementVersions = csvModels.stream().map(new TrafficPointElementCsvToEntityMapper()).toList();
    trafficPointElementVersionRepository.saveAll(trafficPointElementVersions);
  }

  public static List<TrafficPointElementCsvModel> parseTrafficPointElementss(InputStream inputStream)
      throws IOException {
    CsvMapper mapper = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
    CsvSchema csvSchema = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator(';')
        .withEscapeChar('\\');

    MappingIterator<TrafficPointElementCsvModel> mappingIterator = mapper.readerFor(
        TrafficPointElementCsvModel.class).with(csvSchema).readValues(inputStream);
    List<TrafficPointElementCsvModel> trafficPointElements = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      trafficPointElements.add(mappingIterator.next());
    }
    log.info("Parsed {} trafficPointElements", trafficPointElements.size());
    return trafficPointElements;
  }

}
