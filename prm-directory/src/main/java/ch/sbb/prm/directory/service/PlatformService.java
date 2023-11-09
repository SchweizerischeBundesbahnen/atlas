package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ch.sbb.prm.directory.validation.PlatformValidationService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;

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
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    platformRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected PlatformVersion save(PlatformVersion version) {
    boolean reduced = stopPointService.isReduced(version.getParentServicePointSloid());
    platformValidationService.validateRecordingVariants(version,reduced);
    return platformRepository.saveAndFlush(version);
  }

  @Override
  protected List<PlatformVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(PlatformVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(platformRepository));
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public PlatformVersion createPlatformVersion(PlatformVersion version,
                                               SharedServicePointVersionModel sharedServicePointVersionModel) {
    sharedServicePointService.validateTrafficPointElementExists(version.getParentServicePointSloid(), version.getSloid());
    PlatformVersion savedVersion = save(version);
    createRelation(savedVersion);
    return savedVersion;
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public PlatformVersion updatePlatformVersion(PlatformVersion currentVersion, PlatformVersion editedVersion,
                                               SharedServicePointVersionModel sharedServicePointVersionModel) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<PlatformVersion> getPlatformVersionById(Long id) {
    return platformRepository.findById(id);
  }

  public List<PlatformVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return platformRepository.findAllByNumberOrderByValidFrom(number);
  }
}
