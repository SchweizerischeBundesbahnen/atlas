package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult.TrafficPointItemImportResultBuilder;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
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
  private final TrafficPointElementService trafficPointElementService;

  public void importTrafficPointElements(List<TrafficPointElementCsvModel> csvModels) {
    List<TrafficPointElementVersion> trafficPointElementVersions = csvModels.stream()
        .map(new TrafficPointElementCsvToEntityMapper()).toList();

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

  public List<TrafficPointItemImportResult> importTrafficPoints(
      List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers
  ) {
    List<TrafficPointItemImportResult> importResults = new ArrayList<>();
    for (TrafficPointCsvModelContainer container : trafficPointCsvModelContainers) {
      List<TrafficPointElementVersion> trafficPointElementVersions = container.getTrafficPointCsvModelList()
          .stream()
          .map(new TrafficPointElementCsvToEntityMapper())
          .sorted(Comparator.comparing(TrafficPointElementVersion::getValidFrom))
          .toList();
      for (TrafficPointElementVersion trafficPointElementVersion : trafficPointElementVersions) {
        boolean trafficPointElementExisting = trafficPointElementService.isTrafficPointElementExisting(
            trafficPointElementVersion.getSloid());
        if (trafficPointElementExisting) {
          TrafficPointItemImportResult updateResult = updateTrafficPointVersion(trafficPointElementVersion);
          importResults.add(updateResult);
        } else {
          TrafficPointItemImportResult saveResult = saveTrafficPointVersion(trafficPointElementVersion);
          importResults.add(saveResult);
        }
      }
    }
    return importResults;
  }

  private TrafficPointItemImportResult updateTrafficPointVersion(TrafficPointElementVersion trafficPointElementVersion) {
    try {
      trafficPointElementService.updateTrafficPointElementVersion(trafficPointElementVersion);
      return buildSuccessImportResult(trafficPointElementVersion);
    } catch (Exception exception) {
      if (exception instanceof VersioningNoChangesException) {
        log.info("Found version {} to import without modification: {}",
            trafficPointElementVersion.getSloid(),
            exception.getMessage()
        );
        return buildSuccessImportResult(trafficPointElementVersion);
      } else {
        log.error("[Traffic-Point Import]: Error during update with sloid: " + trafficPointElementVersion.getSloid(), exception);
        return buildFailedImportResult(trafficPointElementVersion, exception);
      }
    }
  }

  private TrafficPointItemImportResult saveTrafficPointVersion(TrafficPointElementVersion trafficPointElementVersion) {
    try {
      TrafficPointElementVersion savedTrafficPointVersion = trafficPointElementService.save(trafficPointElementVersion);
      return buildSuccessImportResult(savedTrafficPointVersion);
    } catch (Exception exception) {
      log.error("[Traffic-Point Import]: Error during save with sloid: " + trafficPointElementVersion.getSloid(), exception);
      return buildFailedImportResult(trafficPointElementVersion, exception);
    }
  }

  private TrafficPointItemImportResult buildSuccessImportResult(TrafficPointElementVersion trafficPointElementVersion) {
    TrafficPointItemImportResultBuilder successResultBuilder = TrafficPointItemImportResult.successResultBuilder();
    return addTrafficPointInfoTo(successResultBuilder, trafficPointElementVersion).build();
  }

  private TrafficPointItemImportResult buildFailedImportResult(TrafficPointElementVersion trafficPointElementVersion,
      Exception exception) {
    TrafficPointItemImportResultBuilder failedResultBuilder = TrafficPointItemImportResult.failedResultBuilder(exception);
    return addTrafficPointInfoTo(failedResultBuilder, trafficPointElementVersion).build();
  }

  private TrafficPointItemImportResultBuilder addTrafficPointInfoTo(
      TrafficPointItemImportResultBuilder trafficPointItemImportResultBuilder,
      TrafficPointElementVersion trafficPointElementVersion
  ) {
    return trafficPointItemImportResultBuilder
        .validFrom(trafficPointElementVersion.getValidFrom())
        .validTo(trafficPointElementVersion.getValidTo())
        // TODO: use sloid as itemNumber when possible
        .itemNumber(trafficPointElementVersion.getServicePointNumber().getValue());
  }

}
