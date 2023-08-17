package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.service.BaseImportService;
import ch.sbb.atlas.servicepointdirectory.service.BasePointUtility;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
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
public class LoadingPointImportService extends BaseImportService<LoadingPointVersion> {

  private final LoadingPointService loadingPointService;
  private final VersionableService versionableService;

  @Override
  protected void save(LoadingPointVersion loadingPointVersion) {
    loadingPointService.save(loadingPointVersion);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      LoadingPointVersion loadingPointVersion) {
    return itemImportResultBuilder
        .validFrom(loadingPointVersion.getValidFrom())
        .validTo(loadingPointVersion.getValidTo())
        .itemNumber(getIdentifyingLoadingPointVersionString(loadingPointVersion))
        .build();
  }

  public static List<LoadingPointCsvModel> parseLoadingPoints(InputStream inputStream)
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

  public List<ItemImportResult> importLoadingPoints(
      final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers) {
    final List<ItemImportResult> importResults = new ArrayList<>();
    for (LoadingPointCsvModelContainer container : loadingPointCsvModelContainers) {
      final List<LoadingPointVersion> loadingPointVersions = container.getCsvModelList()
          .stream()
          .map(new LoadingPointCsvToEntityMapper())
          .sorted(Comparator.comparing(LoadingPointVersion::getValidFrom))
          .toList();
      final List<LoadingPointVersion> dbVersions =
          loadingPointService.findLoadingPoint(ServicePointNumber.of(container.getDidokCode()),
              container.getLoadingPointNumber());
      replaceCsvMergedVersions(dbVersions, loadingPointVersions);
      for (LoadingPointVersion loadingPointVersion : loadingPointVersions) {
        final boolean loadingPointExisting = loadingPointService.isLoadingPointExisting(
            loadingPointVersion.getServicePointNumber(),
            loadingPointVersion.getNumber()
        );
        if (loadingPointExisting) {
          importResults.add(updateLoadingPointVersion(loadingPointVersion));
        } else {
          importResults.add(saveLoadingPointVersion(loadingPointVersion));
        }
      }
    }
    return importResults;
  }

  void updateLoadingPointVersionImport(LoadingPointVersion loadingPointVersionEdited) {
    final List<LoadingPointVersion> dbVersions =
        loadingPointService.findLoadingPoint(loadingPointVersionEdited.getServicePointNumber(),
            loadingPointVersionEdited.getNumber());
    final LoadingPointVersion current = BasePointUtility.getCurrentPointVersion(dbVersions, loadingPointVersionEdited);
    final List<VersionedObject> versionedObjects = versionableService.versioningObjectsForImportFromCsv(current,
        loadingPointVersionEdited, dbVersions);
    versionableService.applyVersioning(LoadingPointVersion.class, versionedObjects, loadingPointService::save,
        new ApplyVersioningDeleteByIdLongConsumer(loadingPointService.getLoadingPointVersionRepository()));
  }

  private ItemImportResult updateLoadingPointVersion(LoadingPointVersion loadingPointVersion) {
    try {
      updateLoadingPointVersionImport(loadingPointVersion);
      return buildSuccessImportResult(loadingPointVersion);
    } catch (Exception exception) {
      if (exception instanceof VersioningNoChangesException) {
        log.info("Found version {} to import without modification: {}",
            getIdentifyingLoadingPointVersionString(loadingPointVersion),
            exception.getMessage()
        );
        return buildSuccessImportResult(loadingPointVersion);
      } else {
        log.error("[Loading-Point Import]: Error during update with version: " + getIdentifyingLoadingPointVersionString(
            loadingPointVersion), exception);
        return buildFailedImportResult(loadingPointVersion, exception);
      }
    }
  }

  private ItemImportResult saveLoadingPointVersion(LoadingPointVersion loadingPointVersion) {
    try {
      final LoadingPointVersion savedLoadingPointVersion = loadingPointService.save(loadingPointVersion);
      return buildSuccessImportResult(savedLoadingPointVersion);
    } catch (Exception exception) {
      log.error("[Loading-Point Import]: Error during save with version: " + getIdentifyingLoadingPointVersionString(
          loadingPointVersion), exception);
      return buildFailedImportResult(loadingPointVersion, exception);
    }
  }

  private static String getIdentifyingLoadingPointVersionString(LoadingPointVersion loadingPointVersion) {
    return loadingPointVersion.getServicePointNumber().asString() + "|" + loadingPointVersion.getNumber();
  }

}
