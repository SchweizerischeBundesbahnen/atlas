package ch.sbb.prm.directory.service;

import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPlaceService {

  private final StopPlaceRepository stopPlaceRepository;
  private final VersionableService versionableService;

  public List<StopPlaceVersion> getAllStopPlaces() {
   return stopPlaceRepository.findAll();
  }

  public void checkStopPlaceExists(String sloid) {
    if (!stopPlaceRepository.existsBySloid(sloid)) {
      throw new StopPlaceDoesNotExistsException(sloid);
    }
  }

  public StopPlaceVersion createStopPlace(StopPlaceVersion stopPlaceVersion) {
    return stopPlaceRepository.saveAndFlush(stopPlaceVersion);
  }

  public StopPlaceVersion updateStopPlaceVersion(StopPlaceVersion currentVersion, StopPlaceVersion editedVersion){
    stopPlaceRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(StopPlaceVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSloid(currentVersion.getSloid());

    List<StopPlaceVersion> existingDbVersions = stopPlaceRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(StopPlaceVersion.class, versionedObjects,
        this::createStopPlace, new ApplyVersioningDeleteByIdLongConsumer(stopPlaceRepository));
    return currentVersion;
  }

}
