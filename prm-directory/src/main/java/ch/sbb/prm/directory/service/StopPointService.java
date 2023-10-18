package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class StopPointService extends PrmVersionableService<StopPointVersion> {

  private final StopPointRepository stopPointRepository;

  public StopPointService(StopPointRepository stopPointRepository, VersionableService versionableService) {
    super(versionableService);
    this.stopPointRepository = stopPointRepository;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    stopPointRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected StopPointVersion save(StopPointVersion version) {
    return stopPointRepository.saveAndFlush(version);
  }

  @Override
  protected List<StopPointVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(StopPointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPointRepository));
  }

  public List<StopPointVersion> getAllStopPoints() {
    return stopPointRepository.findAll();
  }

  public List<StopPointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return stopPointRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<StopPointVersion> getStopPointById(Long id) {
    return stopPointRepository.findById(id);
  }

  public void checkStopPointExists(String sloid) {
    if (!stopPointRepository.existsBySloid(sloid)) {
      throw new StopPointDoesNotExistsException(sloid);
    }
  }

  public StopPointVersion createStopPoint(StopPointVersion stopPointVersion) {
    return stopPointRepository.saveAndFlush(stopPointVersion);
  }

  public StopPointVersion updateStopPointVersion(StopPointVersion currentVersion, StopPointVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

}
