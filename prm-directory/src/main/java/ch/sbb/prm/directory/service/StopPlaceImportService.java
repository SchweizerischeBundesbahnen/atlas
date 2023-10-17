package ch.sbb.prm.directory.service;

import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.mapper.StopPlaceVersionMapper;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class StopPlaceImportService extends BasePrmImportService<StopPlaceVersion> {

  private final StopPlaceRepository stopPlaceRepository;
  private final StopPlaceService stopPlaceService;

  private final VersionableService versionableService;

  public StopPlaceImportService(StopPlaceRepository stopPlaceRepository, StopPlaceService stopPlaceService,
      VersionableService versionableService) {
    this.stopPlaceRepository = stopPlaceRepository;
    this.stopPlaceService = stopPlaceService;
    this.versionableService = versionableService;
  }

  @Override
  protected void save(StopPlaceVersion version) {
    stopPlaceService.save(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      StopPlaceVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importServicePoints(@NotNull @NotEmpty List<StopPlaceCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (StopPlaceCsvModelContainer container : csvModelContainers) {
      List<StopPlaceVersion> stopPlaceVersions =
          container.getCreateStopPlaceVersionModels().stream().map(StopPlaceVersionMapper::toEntity).toList();
      List<StopPlaceVersion> dbVersions = stopPlaceService.findAllByNumberOrderByValidFrom(
          ServicePointNumber.ofNumberWithoutCheckDigit(container.getDidokCode()));
      replaceCsvMergedVersions(dbVersions,stopPlaceVersions);
      for (StopPlaceVersion stopPlaceVersion : stopPlaceVersions) {
        boolean stopPlaceExistsByNumber = stopPlaceRepository.existsByNumber(stopPlaceVersion.getNumber());
        ItemImportResult itemImportResult;
        if (stopPlaceExistsByNumber) {
          itemImportResult = updateStopPlace(stopPlaceVersion);
        } else {
          itemImportResult = saveStopPlaceVersion(stopPlaceVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  public void updateStopPlaceVersionForImportService(StopPlaceVersion edited) {
    List<StopPlaceVersion> dbVersions = stopPlaceService.findAllByNumberOrderByValidFrom(edited.getNumber());
    StopPlaceVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    versionableService.applyVersioning(StopPlaceVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPlaceRepository));
  }

  private ItemImportResult updateStopPlace(StopPlaceVersion stopPlaceVersion) {
    try {
      updateStopPlaceVersionForImportService(stopPlaceVersion);
      return buildSuccessImportResult(stopPlaceVersion);
    } catch (Exception exception) {
      if (exception instanceof VersioningNoChangesException) {
        log.info("Found version {} to import without modification: {}",
            stopPlaceVersion.getNumber().getValue(), exception.getMessage());
        return buildSuccessImportResult(stopPlaceVersion);
      } else {
        log.error("[Stop-Place Import]: Error during update", exception);
        return buildFailedImportResult(stopPlaceVersion, exception);
      }
    }
  }

  private ItemImportResult saveStopPlaceVersion(StopPlaceVersion servicePointVersion) {
    try {
      StopPlaceVersion stopPlaceVersion = stopPlaceService.save(servicePointVersion);
      return buildSuccessImportResult(stopPlaceVersion);
    } catch (Exception exception) {
      log.error("[Stop-Place Import]: Error during save", exception);
      return buildFailedImportResult(servicePointVersion, exception);
    }
  }

}
