package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.validation.PlatformValidationService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformService extends PrmRelatableVersionableService<PlatformVersion> {

  private final PlatformRepository platformRepository;
  private final SharedServicePointService sharedServicePointService;
  private final PlatformValidationService platformValidationService;

  public PlatformService(StopPointService stopPointService, RelationService relationService,
      PlatformRepository platformRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService, SharedServicePointService sharedServicePointService,
      PlatformValidationService platformValidationService) {
    super(versionableService, stopPointService, relationService, referencePointRepository);
    this.platformRepository = platformRepository;
    this.sharedServicePointService = sharedServicePointService;
    this.platformValidationService = platformValidationService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PLATFORM;
  }

  @Override
  protected void incrementVersion(String sloid) {
    platformRepository.incrementVersion(sloid);
  }

  @Override
  public PlatformVersion save(PlatformVersion version) {
    boolean reduced = stopPointService.isReduced(version.getParentServicePointSloid());
    platformValidationService.validateRecordingVariants(version,reduced);
    return platformRepository.saveAndFlush(version);
  }

  @Override
  public List<PlatformVersion> getAllVersions(String sloid) {
    return platformRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(PlatformVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(platformRepository));
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public PlatformVersion createPlatformVersion(PlatformVersion version) {
    sharedServicePointService.validateTrafficPointElementExists(version.getParentServicePointSloid(), version.getSloid());
    PlatformVersion savedVersion = save(version);
    createRelation(savedVersion);
    return savedVersion;
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public PlatformVersion updatePlatformVersion(PlatformVersion currentVersion, PlatformVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<PlatformVersion> getPlatformVersionById(Long id) {
    return platformRepository.findById(id);
  }
}
