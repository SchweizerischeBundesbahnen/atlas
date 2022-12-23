package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointImportService {

  private final ServicePointVersionRepository servicePointVersionRepository;

  public void importServicePoints(List<ServicePointCsvModel> servicePointCsvModels) {
    List<ServicePointVersion> servicePointVersions = servicePointCsvModels.stream()
                                                                          .map(
                                                                              new ServicePointCsvToEntityMapper())
                                                                          .toList();
    servicePointVersionRepository.saveAll(servicePointVersions);
  }

  public static List<ServicePointCsvModel> parseServicePoints(InputStream inputStream)
      throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    int counter = 0;
    List<ServicePointCsvModel> servicePoints = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
      counter++;
    }
    log.info("Parsed {} servicePoints", servicePoints.size());
    return servicePoints;
  }

}
