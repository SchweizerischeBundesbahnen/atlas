package ch.sbb.prm.directory.service;

import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.mapper.StopPlaceVersionMapper;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class StopPlaceService extends PrmVersionableService<StopPlaceVersion> {

  private final StopPlaceRepository stopPlaceRepository;
  private final SharedServicePointService sharedServicePointService;

  public StopPlaceService(StopPlaceRepository stopPlaceRepository, VersionableService versionableService, SharedServicePointService sharedServicePointService) {
    super(versionableService);
    this.stopPlaceRepository = stopPlaceRepository;
    this.sharedServicePointService = sharedServicePointService;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    stopPlaceRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected StopPlaceVersion save(StopPlaceVersion version) {
    log.info("save: " +version);
    return stopPlaceRepository.saveAndFlush(version);
  }

  @Override
  protected List<StopPlaceVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(StopPlaceVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPlaceRepository));
  }

  public List<StopPlaceVersion> getAllStopPlaces() {
    return stopPlaceRepository.findAll();
  }

  public List<StopPlaceVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return stopPlaceRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<StopPlaceVersion> getStopPlaceById(Long id) {
    return stopPlaceRepository.findById(id);
  }

  public void checkStopPlaceExists(String sloid) {
    if (!stopPlaceRepository.existsBySloid(sloid)) {
      throw new StopPlaceDoesNotExistsException(sloid);
    }
  }

  public StopPlaceVersion createStopPlace(StopPlaceVersion stopPlaceVersion) {
    sharedServicePointService.validateServicePointExists(stopPlaceVersion.getSloid());

    return stopPlaceRepository.saveAndFlush(stopPlaceVersion);
  }

  public StopPlaceVersion updateStopPlaceVersion(StopPlaceVersion currentVersion, StopPlaceVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public List<ItemImportResult> importServicePoints(@NotNull @NotEmpty List<StopPlaceCsvModelContainer> csvModelContainers) {
    List<ItemImportResult> importResults = new ArrayList<>();
    for(StopPlaceCsvModelContainer container: csvModelContainers){

      List<StopPlaceVersion> stopPlaceVersions =
          container.getCreateStopPlaceVersionModels().stream().map(StopPlaceVersionMapper::toEntity).toList();

      for(StopPlaceVersion stopPlaceVersion : stopPlaceVersions){
        boolean stopPlaceExistsByNumber = stopPlaceRepository.existsByNumber(stopPlaceVersion.getNumber());
        if(stopPlaceExistsByNumber){
          updateStopPlace(stopPlaceVersion);
        }else {
          save(stopPlaceVersion);
        }
      }
    }

  return importResults;
  }

  public void updateStopPlaceVersionForImportService(StopPlaceVersion edited) {
    List<StopPlaceVersion> dbVersions = findAllByNumberOrderByValidFrom(edited.getNumber());
    StopPlaceVersion current = ImportUtils.getCurrentPointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        dbVersions);
//    BasePointUtility.overrideEditionDateAndEditorOnVersionedObjects(edited, versionedObjects);

    versionableService.applyVersioning(StopPlaceVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPlaceRepository));
  }

  private void updateStopPlace(StopPlaceVersion stopPlaceVersion){
    try {
      updateStopPlaceVersionForImportService(stopPlaceVersion);
//      return buildSuccessImportResult(servicePointVersion);
    } catch (Exception exception) {
      if (exception instanceof VersioningNoChangesException) {
        log.info("Found version {} to import without modification: {}",
            stopPlaceVersion.getNumber().getValue(),
            exception.getMessage()
        );
        log.info("fuck -> {}", stopPlaceVersion);
//        return buildSuccessImportResult(servicePointVersion);
      } else {
        log.error("[Service-Point Import]: Error during update", exception);
//        return buildFailedImportResult(servicePointVersion, exception);
      }
    }
  }

}
