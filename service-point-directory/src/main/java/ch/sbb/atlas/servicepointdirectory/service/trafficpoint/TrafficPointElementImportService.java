package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
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

  public static List<TrafficPointElementCsvModel> parseTrafficPointElements(InputStream inputStream)
      throws IOException {
    MappingIterator<TrafficPointElementCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        TrafficPointElementCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<TrafficPointElementCsvModel> trafficPointElements = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      trafficPointElements.add(mappingIterator.next());
    }
    log.info("Parsed {} trafficPointElements", trafficPointElements.size());
    return trafficPointElements;
  }

}
