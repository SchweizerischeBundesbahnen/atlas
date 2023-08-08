package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointItemImportResult;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import ch.sbb.atlas.versioning.service.VersionableService;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadingPointImportService {

  private final LoadingPointService loadingPointService;
  private final VersionableService versionableService;

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

  public List<LoadingPointItemImportResult> importLoadingPoints(
      final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers) {
    final List<LoadingPointItemImportResult> importResults = new ArrayList<>();
    for (LoadingPointCsvModelContainer container : loadingPointCsvModelContainers) {
      final List<LoadingPointVersion> loadingPointVersions = container.getLoadingPointCsvModelList()
          .stream()
          .map(new LoadingPointCsvToEntityMapper())
          .sorted(Comparator.comparing(LoadingPointVersion::getValidFrom))
          .toList();
      for (LoadingPointVersion loadingPointVersion : loadingPointVersions) {
        boolean loadingPointElementExisting = true; // todo: with service
        if (loadingPointElementExisting) {
          importResults.add(updateLoadingPointVersion(loadingPointVersion));
        } else {
          importResults.add(saveLoadingPointVersion(loadingPointVersion));
        }
      }
    }
    return importResults;
  }

  private LoadingPointItemImportResult updateLoadingPointVersion(LoadingPointVersion loadingPointVersion) {

  }

  private LoadingPointItemImportResult saveLoadingPointVersion(LoadingPointVersion loadingPointVersion) {

  }

}
// todo: pre-merge, pre-check, with geolocation versionable property update
