package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.service.ParkingLotService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingLotImportService extends BasePrmImportService<ParkingLotVersion> {

  private final ParkingLotRepository parkingLotRepository;
  private final ParkingLotService parkingLotService;
  private final VersionableService versionableService;

  @Override
  protected void save(ParkingLotVersion version) {
    parkingLotService.saveForImport(version);
  }

  @Override
  protected ItemImportResult addInfoToItemImportResult(ItemImportResult.ItemImportResultBuilder itemImportResultBuilder,
      ParkingLotVersion version) {
    return itemImportResultBuilder
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .itemNumber(version.getNumber().asString())
        .build();
  }

  public List<ItemImportResult> importParkingLots(List<ParkingLotCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for (ParkingLotCsvModelContainer container : csvModelContainers) {
      List<ParkingLotVersion> parkingLotVersions = container.getCreateModels().stream()
          .map(ParkingLotVersionMapper::toEntity).toList();

      List<ParkingLotVersion> dbVersions = parkingLotService.getAllVersions(
          parkingLotVersions.getFirst().getSloid());
      replaceCsvMergedVersions(dbVersions, parkingLotVersions);

      for (ParkingLotVersion parkingLotVersion : parkingLotVersions) {
        boolean exists = parkingLotRepository.existsBySloid(parkingLotVersion.getSloid());
        ItemImportResult itemImportResult;

        if (exists) {
          itemImportResult = updateParkingLot(parkingLotVersion);
        } else {
          itemImportResult = createVersion(parkingLotVersion);
        }
        importResults.add(itemImportResult);
      }
    }
    return importResults;
  }

  private ItemImportResult updateParkingLot(ParkingLotVersion parkingLotVersion) {
    try {
      updateVersionForImportService(parkingLotVersion);
      return buildSuccessImportResult(parkingLotVersion);
    } catch (VersioningNoChangesException exception) {
      log.info("Found version {} to import without modification: {}", parkingLotVersion.getSloid(), exception.getMessage());
      return buildSuccessImportResult(parkingLotVersion);
    } catch (Exception exception) {
      log.error("[ParkingLot Import]: Error during update", exception);
      return buildFailedImportResult(parkingLotVersion, exception);
    }
  }

  private void updateVersionForImportService(ParkingLotVersion edited) {
    List<ParkingLotVersion> dbVersions = parkingLotService.getAllVersions(edited.getSloid());
    ParkingLotVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
    ImportUtils.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);
    versionableService.applyVersioning(ParkingLotVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(parkingLotRepository));
  }

  private ItemImportResult createVersion(ParkingLotVersion parkingLotVersion) {
    try {
      ParkingLotVersion savedVersion = parkingLotService.createParkingLotThroughImport(parkingLotVersion);
      return buildSuccessImportResult(savedVersion);
    } catch (AtlasException exception) {
      log.error("[ParkingLot Import]: Error during save", exception);
      return buildFailedImportResult(parkingLotVersion, exception);
    }
  }

}
