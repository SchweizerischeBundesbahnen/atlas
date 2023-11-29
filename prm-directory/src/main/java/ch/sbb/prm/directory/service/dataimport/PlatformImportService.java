package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.prm.stoppoint.PlatformCsvModelContainer;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlatformImportService extends BasePrmImportService<PlatformVersion> {

  private final PlatformRepository platformRepository;
  private final PlatformService platformService;

  private final VersionableService versionableService;

  public PlatformImportService(PlatformRepository platformRepository, PlatformService platformService,
      VersionableService versionableService) {
    this.platformRepository = platformRepository;
    this.platformService = platformService;
    this.versionableService = versionableService;
  }

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

  public List<ItemImportResult> importPlatforms(@NotNull @NotEmpty List<PlatformCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (PlatformCsvModelContainer container : csvModelContainers) {
      for (List<CreatePlatformVersionModel> createPlatform : container.getModelsGroupedBySloid()){
        List<PlatformVersion> platform = createPlatform.stream().map(PlatformVersionMapper::toEntity).toList();

        List<PlatformVersion> dbVersions =  platformService.getAllVersions(platform.iterator().next().getSloid());
        replaceCsvMergedVersions(dbVersions, platform);

        for (PlatformVersion platformVersion : platform) {
          boolean platformExists = platformRepository.existsBySloid(platformVersion.getSloid());
          ItemImportResult itemImportResult;
          if (platformExists) {
            itemImportResult = updateStopPoint(platformVersion);
          } else {
            itemImportResult = createVersion(platformVersion);
          }
          importResults.add(itemImportResult);
        }
      }
    }
    return importResults;
  }

  private ItemImportResult updateStopPoint(PlatformVersion platformVersion) {
    try {
      updateVersionForImportService(platformVersion);
      return buildSuccessImportResult(platformVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", platformVersion.getSloid(),          exception.getMessage());
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
    versionableService.applyVersioning(PlatformVersion.class, versionedObjects, this::save,        new ApplyVersioningDeleteByIdLongConsumer(platformRepository));
  }

  private ItemImportResult createVersion(PlatformVersion platformVersion) {
    try {
      PlatformVersion savedVersion = platformService.save(platformVersion);
      return buildSuccessImportResult(savedVersion);
    } catch (AtlasException exception) {
      log.error("[Platform Import]: Error during save", exception);
      return buildFailedImportResult(platformVersion, exception);
    }
  }


}
