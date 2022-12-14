package ch.sbb.atlas.servicepointdirectory.service.loading.point;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointRepository;
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
public class LoadingPointImportService {

  private final LoadingPointRepository loadingPointRepository;

  public void importLoadingPoints(List<LoadingPointCsvModel> csvModels) {
    List<LoadingPointVersion> loadingPointVersions = csvModels.stream().map(new LoadingPointCsvToEntityMapper()).toList();
    loadingPointRepository.saveAll(loadingPointVersions);
  }

  static List<LoadingPointCsvModel> parseLoadingPoints(InputStream inputStream)
      throws IOException {
    CsvMapper mapper = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
    CsvSchema csvSchema = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator(';')
        .withEscapeChar('\\');

    MappingIterator<LoadingPointCsvModel> mappingIterator = mapper.readerFor(
        LoadingPointCsvModel.class).with(csvSchema).readValues(inputStream);
    List<LoadingPointCsvModel> loadingPoints = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      loadingPoints.add(mappingIterator.next());
    }
    log.info("Parsed {} loadingPoints", loadingPoints.size());
    return loadingPoints;
  }

}
