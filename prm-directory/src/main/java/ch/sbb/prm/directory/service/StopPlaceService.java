package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StopPlaceService extends PrmVersionableService<StopPlaceVersion> {

  private final StopPlaceRepository stopPlaceRepository;
  private final ServicePointService servicePointService;

  public StopPlaceService(StopPlaceRepository stopPlaceRepository, VersionableService versionableService, ServicePointService servicePointService) {
    super(versionableService);
    this.stopPlaceRepository = stopPlaceRepository;
    this.servicePointService = servicePointService;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    stopPlaceRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected StopPlaceVersion save(StopPlaceVersion version) {
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
    servicePointService.validateServicePointExists(stopPlaceVersion.getSloid());

    return stopPlaceRepository.saveAndFlush(stopPlaceVersion);
  }

  public StopPlaceVersion updateStopPlaceVersion(StopPlaceVersion currentVersion, StopPlaceVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

}
