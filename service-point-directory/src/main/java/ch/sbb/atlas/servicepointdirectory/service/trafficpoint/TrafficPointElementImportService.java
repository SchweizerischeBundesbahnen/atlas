package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.util.DidokCsvMapper;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.HeightNotCalculatableException;
import ch.sbb.atlas.servicepointdirectory.service.BaseImportServicePointDirectoryService;
import ch.sbb.atlas.servicepointdirectory.service.BasePointUtility;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import com.fasterxml.jackson.databind.MappingIterator;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementImportService extends BaseImportServicePointDirectoryService<TrafficPointElementVersion> {

  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;
  private final ServicePointDistributor servicePointDistributor;

  @Override
  protected void save(TrafficPointElementVersion trafficPointElementVersion) {
    trafficPointElementService.save(trafficPointElementVersion);
  }

  @Override
  protected String[] getIgnoredPropertiesWithoutGeolocation() {
    return new String[]{
        TrafficPointElementVersion.Fields.validFrom,
        TrafficPointElementVersion.Fields.validTo,
        TrafficPointElementVersion.Fields.id
    };
  }

  @Override
  protected String[] getIgnoredPropertiesWithGeolocation() {
    return ArrayUtils.add(getIgnoredPropertiesWithoutGeolocation(),
        TrafficPointElementVersion.Fields.trafficPointElementGeolocation);
  }

  @Override
  protected String getIgnoredReferenceFieldOnGeolocationEntity() {
    return TrafficPointElementGeolocation.Fields.trafficPointElementVersion;
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      TrafficPointElementVersion trafficPointElementVersion) {
    return itemImportResultBuilder
        .validFrom(trafficPointElementVersion.getValidFrom())
        .validTo(trafficPointElementVersion.getValidTo())
        .itemNumber(trafficPointElementVersion.getSloid())
        .build();
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

  public List<ItemImportResult> importTrafficPoints(
      List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers
  ) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (TrafficPointCsvModelContainer container : trafficPointCsvModelContainers) {
      //      try {
      //        trafficPointElementService.claimSloid(container.getSloid()); // todo: only new
      //      } catch (FeignException e) {
      //        ItemImportResult importResult = new ItemImportResult();
      //        importResult.setItemNumber(container.getSloid());
      //        importResult.setStatus(ItemImportResponseStatus.FAILED);
      //        importResult.setMessage("[FAILED]: The following sloid could not be claimed: " + container.getSloid());
      //        importResults.add(importResult);
      //        continue;
      //      }

      List<TrafficPointElementVersion> trafficPointElementVersions = container.getCsvModelList()
          .stream()
          .map(new TrafficPointElementCsvToEntityMapper())
          .sorted(Comparator.comparing(TrafficPointElementVersion::getValidFrom))
          .toList();

      List<TrafficPointElementVersion> dbVersions = trafficPointElementService.findBySloidOrderByValidFrom(container.getSloid());
      replaceCsvMergedVersions(dbVersions, trafficPointElementVersions);

      for (TrafficPointElementVersion trafficPointElementVersion : trafficPointElementVersions) {
        boolean trafficPointElementExisting = trafficPointElementService.isTrafficPointElementExisting(
            trafficPointElementVersion.getSloid());
        if (trafficPointElementExisting) {
          ItemImportResult updateResult = updateTrafficPointVersion(trafficPointElementVersion);
          importResults.add(updateResult);
        } else {
          ItemImportResult saveResult = saveTrafficPointVersion(trafficPointElementVersion);
          importResults.add(saveResult);
        }
      }

      servicePointDistributor.publishTrafficPointElements(trafficPointElementVersions);
    }
    return importResults;
  }

  void updateTrafficPointElementVersionImport(TrafficPointElementVersion edited) {
    List<TrafficPointElementVersion> dbVersions = trafficPointElementService.findBySloidOrderByValidFrom(edited.getSloid());
    TrafficPointElementVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    BasePointUtility.addCreateAndEditDetailsToGeolocationPropertyFromVersionedObjects(versionedObjects,
        TrafficPointElementVersion.Fields.trafficPointElementGeolocation);
    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, trafficPointElementService::save,
        new ApplyVersioningDeleteByIdLongConsumer(trafficPointElementService.getTrafficPointElementVersionRepository()));
  }

  private ItemImportResult updateTrafficPointVersion(TrafficPointElementVersion trafficPointElementVersion) {
    List<Exception> warnings = new ArrayList<>();
    getHeightForTrafficPoint(trafficPointElementVersion, warnings);
    try {
      updateTrafficPointElementVersionImport(trafficPointElementVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}",
          trafficPointElementVersion.getSloid(),
          exception.getMessage()
      );
      return buildSuccessImportResult(trafficPointElementVersion);
    } catch (Exception exception) {
      log.error("[Traffic-Point Import]: Error during update with sloid: " + trafficPointElementVersion.getSloid(), exception);
      return buildFailedImportResult(trafficPointElementVersion, exception);
    }

    return buildWarningMessage(trafficPointElementVersion, warnings);
  }

  private ItemImportResult saveTrafficPointVersion(TrafficPointElementVersion trafficPointElementVersion) {
    List<Exception> warnings = new ArrayList<>();
    getHeightForTrafficPoint(trafficPointElementVersion, warnings);
    try {
      trafficPointElementService.createThroughImport(trafficPointElementVersion);
    } catch (Exception exception) {
      log.error("[Traffic-Point Import]: Error during save with sloid: " + trafficPointElementVersion.getSloid(), exception);
      return buildFailedImportResult(trafficPointElementVersion, exception);
    }

    return buildWarningMessage(trafficPointElementVersion, warnings);
  }

  private void getHeightForTrafficPoint(TrafficPointElementVersion trafficPointElementVersion, List<Exception> warnings) {
    try {
      trafficPointElementService.setHeightForTrafficPoints(trafficPointElementVersion);
    } catch (HeightNotCalculatableException exception) {
      log.warn("[Traffic-Point Import]: Warning during height calculation ", exception);
      warnings.add(exception);
    }
  }

  private ItemImportResult buildWarningMessage(TrafficPointElementVersion trafficPointElementVersion, List<Exception> warnings) {
    if (!warnings.isEmpty()) {
      return buildWarningImportResult(trafficPointElementVersion, warnings);
    } else {
      return buildSuccessImportResult(trafficPointElementVersion);
    }
  }

}
