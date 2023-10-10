package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformService extends RelatableService<PlatformVersion> {

  private final PlatformRepository platformRepository;
  private final VersionableService versionableService;

  public PlatformService(StopPlaceService stopPlaceService, RelationService relationService,
      PlatformRepository platformRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService) {
    super(stopPlaceService, relationService, referencePointRepository);
    this.platformRepository = platformRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PLATFORM;
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  public PlatformVersion createPlatformVersion(PlatformVersion version) {
    createRelation(version);
    return platformRepository.saveAndFlush(version);
  }

  public PlatformVersion updateStopPlaceVersion(PlatformVersion currentVersion, PlatformVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<PlatformVersion> existingDbVersions = platformRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(PlatformVersion.class, versionedObjects,
        this::createPlatformVersion, new ApplyVersioningDeleteByIdLongConsumer(platformRepository));
    return currentVersion;
  }

  private void checkStaleObjectIntegrity(PlatformVersion currentVersion, PlatformVersion editedVersion) {
    platformRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(PlatformVersion.class.getSimpleName(), "version");
    }
  }

  public Optional<PlatformVersion> getStopPlaceById(Long id) {
    return platformRepository.findById(id);
  }

  public List<PlatformVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return platformRepository.findAllByNumberOrderByValidFrom(number);
  }
}
