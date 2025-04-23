package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.PlatformAlreadyExistsException;
import ch.sbb.prm.directory.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.search.PlatformSearchRestrictions;
import ch.sbb.prm.directory.util.PlatformRecordingStatusEvaluator;
import ch.sbb.prm.directory.validation.PlatformValidationService;
import ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
      PlatformValidationService platformValidationService, PrmLocationService locationService) {
    super(versionableService, stopPointService, relationService, referencePointRepository, locationService);
    this.platformRepository = platformRepository;
    this.sharedServicePointService = sharedServicePointService;
    this.platformValidationService = platformValidationService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PLATFORM;
  }

  @Override
  protected SloidType getSloidType() {
    return SloidType.PLATFORM;
  }

  @Override
  protected void incrementVersion(String sloid) {
    platformRepository.incrementVersion(sloid);
  }

  @Override
  public PlatformVersion save(PlatformVersion version) {
    Set<MeanOfTransport> meanOfTransports = stopPointService.getMeansOfTransportOfAllVersions(version.getParentServicePointSloid());
    PlatformVersionMapper.initDefaultDropdownData(version, meanOfTransports);
    platformValidationService.validateRecordingVariants(version, PrmMeansOfTransportHelper.isReduced(meanOfTransports));
    platformValidationService.validatePreconditions(version, meanOfTransports);
    initDefaultData(version);
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
    boolean isPlatformExisting = isPlatformExisting(version.getSloid());
    if (isPlatformExisting) {
      throw new PlatformAlreadyExistsException(version.getSloid());
    }
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

      PlatformVersion platformVersion = OverviewDisplayBuilder.mergeVersionsForDisplay(versions,
          PlatformVersion::getSloid).getFirst();

      overviewModels.add(PlatformOverviewModel.builder()
          .sloid(sloid)
          .validFrom(platformVersion.getValidFrom())
          .validTo(platformVersion.getValidTo())
          .recordingStatus(getRecordingStatusIncludingRelation(sloid,
              PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, reduced)))
          .build());
    });
    return overviewModels;
  }

  public void checkPlatformExists(String sloid, String type) {
    if (!platformRepository.existsBySloid(sloid)) {
      throw new ElementTypeDoesNotExistException(sloid, type);
    }
  }

  public boolean isPlatformExisting(String sloid) {
    return platformRepository.existsBySloid(sloid);
  }

  @Transactional
  public void updateAttentionFieldByParentSloid(String parentServicePointSloid, Set<MeanOfTransport> newMeansOfTransport) {
    boolean attentionFieldAllowed = PrmMeansOfTransportHelper.isAttentionFieldAllowed(newMeansOfTransport);

    List<PlatformVersion> platformVersions = platformRepository.findAllByParentServicePointSloid(parentServicePointSloid);
    for (PlatformVersion platformVersion : platformVersions) {
      if (attentionFieldAllowed && platformVersion.getAttentionField() == null) {
        platformVersion.setAttentionField(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      }
      if (!attentionFieldAllowed && platformVersion.getAttentionField() != null) {
        platformVersion.setAttentionField(null);
      }
    }
  }
}
