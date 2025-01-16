package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.CONTACT_POINT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.ObjectRevokedException;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ReferencePointSearchRestrictions;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReferencePointService extends PrmVersionableService<ReferencePointVersion> {

  private final ReferencePointRepository referencePointRepository;
  private final ToiletRepository toiletRepository;
  private final ContactPointRepository contactPointRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final PlatformRepository platformRepository;
  private final RelationService relationService;
  private final StopPointService stopPointService;
  protected final PrmLocationService locationService;
  private final ReferencePointValidationService referencePointValidationService;

  public ReferencePointService(ReferencePointRepository referencePointRepository,
      ToiletRepository toiletRepository, ContactPointRepository contactPointRepository,
      ParkingLotRepository parkingLotRepository, PlatformRepository platformRepository, RelationService relationService,
      StopPointService stopPointService, VersionableService versionableService,
      PrmLocationService locationService, ReferencePointValidationService referencePointValidationService) {
    super(versionableService);
    this.referencePointRepository = referencePointRepository;
    this.toiletRepository = toiletRepository;
    this.contactPointRepository = contactPointRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.platformRepository = platformRepository;
    this.relationService = relationService;
    this.stopPointService = stopPointService;
    this.locationService = locationService;
    this.referencePointValidationService = referencePointValidationService;
  }

  @Override
  protected void incrementVersion(String sloid) {
    referencePointRepository.incrementVersion(sloid);
  }

  @Override
  public ReferencePointVersion save(ReferencePointVersion version) {
    referencePointValidationService.validatePreconditionBusinessRule(version);
    stopPointService.validateIsNotReduced(version.getParentServicePointSloid());
    initDefaultData(version);
    return referencePointRepository.saveAndFlush(version);
  }

  @Override
  public List<ReferencePointVersion> getAllVersions(String sloid) {
    return referencePointRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ReferencePointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(referencePointRepository));
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#referencePointVersion)")
  public ReferencePointVersion createReferencePoint(ReferencePointVersion referencePointVersion) {
    stopPointService.checkStopPointExists(referencePointVersion.getParentServicePointSloid());
    stopPointService.validateIsNotReduced(referencePointVersion.getParentServicePointSloid());
    referencePointValidationService.validatePreconditionBusinessRule(referencePointVersion);
    locationService.allocateSloid(referencePointVersion, SloidType.REFERENCE_POINT);
    setStatusToValidate(referencePointVersion);
    searchAndUpdatePlatformRelation(referencePointVersion);
    searchAndUpdateToiletRelation(referencePointVersion);
    searchAndUpdateContactPoint(referencePointVersion);
    searchAndUpdateParkingLot(referencePointVersion);

    return referencePointRepository.saveAndFlush(referencePointVersion);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ReferencePointVersion updateReferencePointVersion(ReferencePointVersion currentVersion,
      ReferencePointVersion editedVersion) {
    if (currentVersion.getStatus() == Status.REVOKED) {
      throw new ObjectRevokedException(ReferencePointVersion.class, currentVersion.getSloid());
    }
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ReferencePointVersion> getReferencePointById(Long id) {
    return referencePointRepository.findById(id);
  }

  private void searchAndUpdateParkingLot(ReferencePointVersion referencePointVersion) {
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(
        referencePointVersion.getParentServicePointSloid());
    searchAndUpdateVersion(parkingLotVersions, referencePointVersion, PARKING_LOT);
  }

  private void searchAndUpdateContactPoint(ReferencePointVersion referencePointVersion) {
    List<ContactPointVersion> contactPointVersions = contactPointRepository.findByParentServicePointSloid(
        referencePointVersion.getParentServicePointSloid());
    searchAndUpdateVersion(contactPointVersions, referencePointVersion, CONTACT_POINT);
  }

  private void searchAndUpdatePlatformRelation(ReferencePointVersion referencePointVersion) {
    List<PlatformVersion> platformVersions =
        platformRepository.findByParentServicePointSloid(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateVersion(platformVersions, referencePointVersion, PLATFORM);
  }

  private void searchAndUpdateToiletRelation(ReferencePointVersion referencePointVersion) {
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(
        referencePointVersion.getParentServicePointSloid());
    searchAndUpdateVersion(toiletVersions, referencePointVersion, TOILET);
  }

  private void searchAndUpdateVersion(List<? extends Relatable> versions, ReferencePointVersion referencePointVersion,
      ReferencePointElementType referencePointElementType) {
    versions.stream().collect(Collectors.groupingBy(Relatable::getSloid))
        .forEach((sloid, relatedVersions) -> relationService.save(RelationUtil.buildRelationVersion(relatedVersions,
            referencePointVersion, referencePointElementType)));
  }

  public Page<ReferencePointVersion> findAll(ReferencePointSearchRestrictions searchRestrictions) {
    return referencePointRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<ReferencePointVersion> findByParentServicePointSloid(String parentServicePointSloid) {
    return referencePointRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public List<ReadReferencePointVersionModel> buildOverview(List<ReferencePointVersion> referencePointVersions) {
    List<ReferencePointVersion> mergedVersions = OverviewDisplayBuilder.mergeVersionsForDisplay(referencePointVersions,
        ReferencePointVersion::getSloid);
    return mergedVersions.stream().map(ReferencePointVersionMapper::toModel).toList();
  }

  public void checkReferencePointExists(String sloid, String type) {
    if (!referencePointRepository.existsBySloid(sloid)) {
      throw new ElementTypeDoesNotExistException(sloid, type);
    }
  }

}
