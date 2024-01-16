package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import ch.sbb.atlas.service.OverviewService;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.search.PlatformSearchRestrictions;
import ch.sbb.prm.directory.validation.PlatformValidationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
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
      PlatformValidationService platformValidationService, LocationClient locationClient) {
    super(versionableService, stopPointService, relationService, referencePointRepository, locationClient);
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
    version.setEditionDate(LocalDateTime.now());
    version.setEditor(UserService.getUserIdentifier());

    boolean reduced = stopPointService.isReduced(version.getParentServicePointSloid());
    platformValidationService.validateRecordingVariants(version, reduced);
    return platformRepository.saveAndFlush(version);
  }

  public PlatformVersion saveForImport(PlatformVersion version) {
    boolean reduced = stopPointService.isReduced(version.getParentServicePointSloid());
    platformValidationService.validateRecordingVariants(version, reduced);
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

  public Page<PlatformVersion> findAll(PlatformSearchRestrictions searchRestrictions) {
    return platformRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<PlatformVersion> getPlatformsByStopPoint(String sloid) {
    PlatformSearchRestrictions searchRestrictions = PlatformSearchRestrictions.builder()
        .prmObjectRequestParams(PrmObjectRequestParams.builder()
            .parentServicePointSloids(List.of(sloid))
            .build())
        .build();

    return platformRepository.findAll(searchRestrictions.getSpecification());
  }

  public List<PlatformOverviewModel> mergePlatformsForOverview(List<PlatformVersion> platforms, String parentSloid) {
    boolean reduced = stopPointService.isReduced(parentSloid);

    Map<String, List<PlatformVersion>> groupedPlatforms = platforms.stream()
        .collect(Collectors.groupingBy(PlatformVersion::getSloid));

    List<PlatformOverviewModel> overviewModels = new ArrayList<>();
    groupedPlatforms.forEach((sloid, versions) -> {
      versions.sort(Comparator.comparing(PlatformVersion::getValidFrom));

      PlatformVersion platformVersion = OverviewService.mergeVersionsForDisplay(versions,
          (previous, current) -> previous.getSloid().equals(current.getSloid())).iterator().next();

      overviewModels.add(PlatformOverviewModel.builder()
          .sloid(sloid)
          .validFrom(platformVersion.getValidFrom())
          .validTo(platformVersion.getValidTo())
          .recordingStatus(PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, reduced))
          .build());
    });
    return overviewModels;
  }


}
