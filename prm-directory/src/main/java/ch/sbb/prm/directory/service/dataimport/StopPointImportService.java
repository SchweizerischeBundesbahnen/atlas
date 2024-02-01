package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.StopPointService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StopPointImportService extends BasePrmImportService<StopPointVersion> {

  private final StopPointRepository stopPointRepository;
  private final StopPointService stopPointService;

  private final VersionableService versionableService;

  public StopPointImportService(StopPointRepository stopPointRepository, StopPointService stopPointService,
      VersionableService versionableService) {
    this.stopPointRepository = stopPointRepository;
    this.stopPointService = stopPointService;
    this.versionableService = versionableService;
  }

  @Override
  protected void save(StopPointVersion version) {
    stopPointService.saveForImport(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      StopPointVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importServicePoints(@NotNull @NotEmpty List<StopPointCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (StopPointCsvModelContainer container : csvModelContainers) {
      List<StopPointVersion> stopPointVersions =
          container.getStopPointVersionModels().stream().map(StopPointVersionMapper::toEntity).toList();
      List<StopPointVersion> dbVersions = stopPointService.findAllByNumberOrderByValidFrom(
          ServicePointNumber.ofNumberWithoutCheckDigit(container.getDidokCode()));
      replaceCsvMergedVersions(dbVersions, stopPointVersions);
      for (StopPointVersion stopPointVersion : stopPointVersions) {
        boolean stopPointExistsByNumber = stopPointRepository.existsByNumber(stopPointVersion.getNumber());
        ItemImportResult itemImportResult;
        if (stopPointExistsByNumber) {
          itemImportResult = updateStopPoint(stopPointVersion);
        } else {
          itemImportResult = saveStopPointVersion(stopPointVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  public void updateStopPointVersionForImportService(StopPointVersion edited) {
    List<StopPointVersion> dbVersions = stopPointService.findAllByNumberOrderByValidFrom(edited.getNumber());
    StopPointVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    versionableService.applyVersioning(StopPointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPointRepository));
  }

  private ItemImportResult updateStopPoint(StopPointVersion stopPointVersion) {
    try {
      updateStopPointVersionForImportService(stopPointVersion);
      return buildSuccessImportResult(stopPointVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", stopPointVersion.getNumber().getValue(),
          exception.getMessage());
      return buildSuccessImportResult(stopPointVersion);
    } catch (Exception exception) {
      log.error("[Stop-Point Import]: Error during update", exception);
      return buildFailedImportResult(stopPointVersion, exception);
    }
  }

  private ItemImportResult saveStopPointVersion(StopPointVersion servicePointVersion) {
    try {
      StopPointVersion stopPointVersion = stopPointService.saveForImport(servicePointVersion);
      return buildSuccessImportResult(stopPointVersion);
    } catch (AtlasException exception) {
      log.error("[Stop-Point Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }
  }


}
