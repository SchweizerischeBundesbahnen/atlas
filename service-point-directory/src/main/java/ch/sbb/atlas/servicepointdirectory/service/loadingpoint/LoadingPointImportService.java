package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
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
public class LoadingPointImportService {

  private final LoadingPointVersionRepository loadingPointVersionRepository;

  static List<LoadingPointCsvModel> parseLoadingPoints(InputStream inputStream)
      throws IOException {
    MappingIterator<LoadingPointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        LoadingPointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(inputStream);
    List<LoadingPointCsvModel> loadingPoints = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      loadingPoints.add(mappingIterator.next());
    }
    log.info("Parsed {} loadingPoints", loadingPoints.size());
    return loadingPoints;
  }

  public void importLoadingPoints(List<LoadingPointCsvModel> csvModels) {
    List<LoadingPointVersion> loadingPointVersions = csvModels.stream().map(new LoadingPointCsvToEntityMapper()).toList();
    loadingPointVersionRepository.saveAll(loadingPointVersions);
  }

}
