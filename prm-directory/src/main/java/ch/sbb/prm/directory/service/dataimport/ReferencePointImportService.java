package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.service.ReferencePointService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencePointImportService extends BasePrmImportService<ReferencePointVersion> {

  private final ReferencePointRepository referencePointRepository;
  private final ReferencePointService referencePointService;
  private final VersionableService versionableService;

  @Override
  protected void save(ReferencePointVersion version) {
    referencePointService.saveForImport(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResult.ItemImportResultBuilder itemImportResultBuilder,
      ReferencePointVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importReferencePoints(List<ReferencePointCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ReferencePointCsvModelContainer container : csvModelContainers) {
      List<ReferencePointVersion> referencePointVersions = container.getCreateModels().stream()
          .map(ReferencePointVersionMapper::toEntity).toList();

      List<ReferencePointVersion> dbVersions = referencePointService.getAllVersions(
          referencePointVersions.iterator().next().getSloid());
      replaceCsvMergedVersions(dbVersions, referencePointVersions);

      for (ReferencePointVersion referencePointVersion : referencePointVersions) {
        boolean referencePointExists = referencePointRepository.existsBySloid(referencePointVersion.getSloid());
        ItemImportResult itemImportResult;

        if (referencePointExists) {
          itemImportResult = updateReferencePoint(referencePointVersion);
        } else {
          itemImportResult = createVersion(referencePointVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  private ItemImportResult updateReferencePoint(ReferencePointVersion referencePointVersion) {
    try {
      updateVersionForImportService(referencePointVersion);
      return buildSuccessImportResult(referencePointVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", referencePointVersion.getSloid(), exception.getMessage());
      return buildSuccessImportResult(referencePointVersion);
    } catch (Exception exception) {
      log.error("[ReferencePoint Import]: Error during update", exception);
      return buildFailedImportResult(referencePointVersion, exception);
    }
  }

  private void updateVersionForImportService(ReferencePointVersion edited) {
    List<ReferencePointVersion> dbVersions = referencePointService.getAllVersions(edited.getSloid());
    ReferencePointVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    versionableService.applyVersioning(ReferencePointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(referencePointRepository));
  }

  private ItemImportResult createVersion(ReferencePointVersion referencePointVersion) {
    try {
      ReferencePointVersion savedVersion = referencePointService.createReferencePointThroughImport(referencePointVersion);
      return buildSuccessImportResult(savedVersion);
    } catch (AtlasException exception) {
      log.error("[ReferencePoint Import]: Error during save", exception);
      return buildFailedImportResult(referencePointVersion, exception);
    }
  }

}
