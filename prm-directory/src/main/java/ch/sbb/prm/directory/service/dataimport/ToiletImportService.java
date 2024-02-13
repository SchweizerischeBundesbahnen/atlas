package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.service.ToiletService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToiletImportService extends BasePrmImportService<ToiletVersion> {

  private final ToiletRepository toiletRepository;
  private final ToiletService toiletService;
  private final VersionableService versionableService;

  @Override
  protected void save(ToiletVersion version) {
    toiletService.saveForImport(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResult.ItemImportResultBuilder itemImportResultBuilder,
      ToiletVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importToiletPoints(List<ToiletCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ToiletCsvModelContainer container : csvModelContainers) {
      List<ToiletVersion> toiletVersions = container.getCreateModels().stream()
          .map(ToiletVersionMapper::toEntity).toList();

      List<ToiletVersion> dbVersions = toiletService.getAllVersions(
          toiletVersions.iterator().next().getSloid());
      replaceCsvMergedVersions(dbVersions, toiletVersions);

      for (ToiletVersion toiletVersion : toiletVersions) {
        boolean toiletExists = toiletRepository.existsBySloid(toiletVersion.getSloid());
        ItemImportResult itemImportResult;

        if (toiletExists) {
          itemImportResult = updateToilet(toiletVersion);
        } else {
          itemImportResult = createVersion(toiletVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  private ItemImportResult updateToilet(ToiletVersion toiletVersion) {
    try {
      updateVersionForImportService(toiletVersion);
      return buildSuccessImportResult(toiletVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", toiletVersion.getSloid(), exception.getMessage());
      return buildSuccessImportResult(toiletVersion);
    } catch (Exception exception) {
      log.error("[Toilet Import]: Error during update", exception);
      return buildFailedImportResult(toiletVersion, exception);
    }
  }

  private void updateVersionForImportService(ToiletVersion edited) {
    List<ToiletVersion> dbVersions = toiletService.getAllVersions(edited.getSloid());
    ToiletVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    versionableService.applyVersioning(ToiletVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(toiletRepository));
  }

  private ItemImportResult createVersion(ToiletVersion toiletVersion) {
    try {
      ToiletVersion savedVersion = toiletService.createToiletPointThroughImport(toiletVersion);
      return buildSuccessImportResult(savedVersion);
    } catch (AtlasException exception) {
      log.error("[Toilet Import]: Error during save", exception);
      return buildFailedImportResult(toiletVersion, exception);
    }
  }

}
