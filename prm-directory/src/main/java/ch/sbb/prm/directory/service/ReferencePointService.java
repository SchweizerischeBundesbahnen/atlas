package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.CONTACT_POINT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.service.OverviewService;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ReferencePointSearchRestrictions;
import ch.sbb.prm.directory.util.RelationUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    version.setEditionDate(LocalDateTime.now());
    version.setEditor(UserService.getUserIdentifier());
    referencePointValidationService.validatePreconditionBusinessRule(version);
    stopPointService.validateIsNotReduced(version.getParentServicePointSloid());
    return referencePointRepository.saveAndFlush(version);
  }

  public ReferencePointVersion saveForImport(ReferencePointVersion version) {
    stopPointService.validateIsNotReduced(version.getParentServicePointSloid());
    return referencePointRepository.saveAndFlush(version);
  }

  public ReferencePointVersion createReferencePointThroughImport(ReferencePointVersion version) {
    stopPointService.validateIsNotReduced(version.getParentServicePointSloid());
    locationService.allocateSloid(version, SloidType.REFERENCE_POINT);
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
    locationService.allocateSloid(referencePointVersion, SloidType.REFERENCE_POINT);
    referencePointValidationService.validatePreconditionBusinessRule(referencePointVersion);

    searchAndUpdatePlatformRelation(referencePointVersion.getParentServicePointSloid(), referencePointVersion.getSloid());
    searchAndUpdateToiletRelation(referencePointVersion.getParentServicePointSloid(), referencePointVersion.getSloid());
    searchAndUpdateContactPoint(referencePointVersion.getParentServicePointSloid(), referencePointVersion.getSloid());
    searchAndUpdateParkingLot(referencePointVersion.getParentServicePointSloid(), referencePointVersion.getSloid());

    return referencePointRepository.saveAndFlush(referencePointVersion);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ReferencePointVersion updateReferencePointVersion(ReferencePointVersion currentVersion,
      ReferencePointVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ReferencePointVersion> getReferencePointById(Long id) {
    return referencePointRepository.findById(id);
  }

  private void searchAndUpdateParkingLot(String parentServicePointSloid, String referencePointSloid) {
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    searchAndUpdateVersion(parkingLotVersions, referencePointSloid, PARKING_LOT);
  }

  private void searchAndUpdateContactPoint(String parentServicePointSloid, String referencePointSloid) {
    List<ContactPointVersion> contactPointVersions = contactPointRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    searchAndUpdateVersion(contactPointVersions, referencePointSloid, CONTACT_POINT);
  }

  private void searchAndUpdatePlatformRelation(String parentServicePointSloid, String referencePointSloid) {
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(parentServicePointSloid);
    searchAndUpdateVersion(platformVersions, referencePointSloid, PLATFORM);
  }

  private void searchAndUpdateToiletRelation(String parentServicePointSloid, String referencePointSloid) {
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(parentServicePointSloid);
    searchAndUpdateVersion(toiletVersions, referencePointSloid, TOILET);
  }

  private void searchAndUpdateVersion(List<? extends Relatable> versions, String referencePointSloid,
      ReferencePointElementType referencePointElementType) {
    versions.forEach(
        version -> relationService.save(RelationUtil.buildRelationVersion(version, referencePointSloid,
            referencePointElementType)));
  }

  public Page<ReferencePointVersion> findAll(ReferencePointSearchRestrictions searchRestrictions) {
    return referencePointRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<ReferencePointVersion> findByParentServicePointSloid(String parentServicePointSloid) {
    return referencePointRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public Container<ReadReferencePointVersionModel> buildOverview(List<ReferencePointVersion> referencePointVersions,
      Pageable pageable) {
    List<ReferencePointVersion> mergedVersions = OverviewService.mergeVersionsForDisplay(referencePointVersions,
        (x, y) -> x.getSloid().equals(y.getSloid()));
    return OverviewService.toPagedContainer(mergedVersions.stream().map(ReferencePointVersionMapper::toModel).toList(),
        pageable);
  }

}
