package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.service.PlatformService;
import ch.sbb.prm.directory.service.SharedServicePointService;
import ch.sbb.prm.directory.service.StopPointService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformImportService extends BasePrmImportService<PlatformVersion> {

  private final PlatformRepository platformRepository;
  private final PlatformService platformService;
  private final VersionableService versionableService;
  private final StopPointService stopPointService;
  private final SharedServicePointService sharedServicePointService;

  @Override
  protected void save(PlatformVersion version) {
    platformService.save(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
      PlatformVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importPlatforms(List<PlatformCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (PlatformCsvModelContainer container : csvModelContainers) {
      List<PlatformVersion> csvVersions = container.getCreateModels().stream().map(PlatformVersionMapper::toEntity).toList();
      csvVersions.forEach(this::clearVariantDependentProperties);

      List<PlatformVersion> dbVersions = platformService.getAllVersions(csvVersions.iterator().next().getSloid());
      replaceCsvMergedVersions(dbVersions, csvVersions);

      for (PlatformVersion platformVersion : csvVersions) {
        boolean platformExists = platformRepository.existsBySloid(platformVersion.getSloid());
        ItemImportResult itemImportResult;
        if (platformExists) {
          itemImportResult = updatePlatform(platformVersion);
        } else {
          itemImportResult = createVersion(platformVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  private ItemImportResult updatePlatform(PlatformVersion platformVersion) {
    try {
      updateVersionForImportService(platformVersion);
      return buildSuccessImportResult(platformVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", platformVersion.getSloid(), exception.getMessage());
      return buildSuccessImportResult(platformVersion);
    } catch (Exception exception) {
      log.error("[Platform Import]: Error during update", exception);
      return buildFailedImportResult(platformVersion, exception);
    }
  }

  private void updateVersionForImportService(PlatformVersion edited) {
    List<PlatformVersion> dbVersions = platformService.getAllVersions(edited.getSloid());
    PlatformVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    versionableService.applyVersioning(PlatformVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(platformRepository));
  }

  private ItemImportResult createVersion(PlatformVersion platformVersion) {
    try {
      sharedServicePointService.validateTrafficPointElementExists(platformVersion.getParentServicePointSloid(),
          platformVersion.getSloid());
      PlatformVersion savedVersion = platformService.save(platformVersion);
      return buildSuccessImportResult(savedVersion);
    } catch (AtlasException exception) {
      log.error("[Platform Import]: Error during save", exception);
      return buildFailedImportResult(platformVersion, exception);
    }
  }

  private void clearVariantDependentProperties(PlatformVersion version) {
    boolean reduced = stopPointService.isReduced(version.getParentServicePointSloid());
    if (reduced) {
      version.setBoardingDevice(null);
      version.setAdviceAccessInfo(null);
      version.setContrastingAreas(null);
      version.setDynamicAudio(null);
      version.setDynamicVisual(null);
      version.setInclination(null);
      version.setInclinationWidth(null);
      version.setLevelAccessWheelchair(null);
      version.setSuperelevation(null);
    } else {
      version.setHeight(null);
      version.setInclinationLongitudinal(null);
      version.setInfoOpportunities(null);
      version.setPartialElevation(null);
      version.setTactileSystem(null);
      version.setVehicleAccess(null);
      version.setWheelchairAreaLength(null);
      version.setWheelchairAreaWidth(null);
    }
  }

}
