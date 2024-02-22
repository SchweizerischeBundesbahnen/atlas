package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.search.RelationSearchRestrictions;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RelationService extends PrmVersionableService<RelationVersion> {

  private final StopPointService stopPointService;
  private final RelationRepository relationRepository;
  private final ReferencePointService referencePointService;
  private final ToiletService toiletService;
  private final ParkingLotService parkingLotService;
  private final ContactPointService contactPointService;
  private final PlatformService platformService;

  public RelationService(RelationRepository relationRepository, VersionableService versionableService,
      StopPointService stopPointService, ReferencePointService referencePointService, ToiletService toiletService,
                         ParkingLotService parkingLotService,  ContactPointService contactPointService, PlatformService platformService) {
    super(versionableService);
    this.relationRepository = relationRepository;
    this.referencePointService = referencePointService;
    this.stopPointService = stopPointService;
    this.toiletService = toiletService;
    this.parkingLotService = parkingLotService;
    this.contactPointService = contactPointService;
    this.platformService = platformService;
  }

  public List<RelationVersion> getRelationsBySloid(String sloid) {
    return relationRepository.findAllBySloid(sloid);
  }

  public List<RelationVersion> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referencePointType) {
    return relationRepository.findAllBySloidAndReferencePointElementType(sloid, referencePointType);
  }

  public List<RelationVersion> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid,
      ReferencePointElementType referencePointType) {
    return relationRepository.findAllByParentServicePointSloidAndReferencePointElementType(parentServicePointSloid,
        referencePointType);
  }

  public List<RelationVersion> getRelationsByParentServicePointSloid(String parentServicePointSloid) {
    return relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
  }

  @Override
  protected void incrementVersion(String sloid) {
    relationRepository.incrementVersion(sloid);
  }

  @Override
  public RelationVersion save(RelationVersion relationVersion) {
    stopPointService.validateIsNotReduced(relationVersion.getParentServicePointSloid());

    setEditionDateAndEditor(relationVersion);
    return relationRepository.saveAndFlush(relationVersion);
  }

  @Override
  public List<RelationVersion> getAllVersions(String sloid) {
    return relationRepository.findAllBySloid(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(RelationVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(relationRepository));
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public RelationVersion updateRelationVersion(RelationVersion currentVersion, RelationVersion editedVersion) {
    //the referencePointTypeElement cannot be updated. We have to set it from the current version
    editedVersion.setReferencePointElementType(currentVersion.getReferencePointElementType());
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<RelationVersion> getRelationById(Long id) {
    return relationRepository.findById(id);
  }

  public Page<RelationVersion> findAll(RelationSearchRestrictions searchRestrictions) {
    return relationRepository.findAll(searchRestrictions.getSpecification(),searchRestrictions.getPageable());
  }

  public RelationVersion createRelationThroughImport(RelationVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());

    if(stopPointService.isReduced(version.getParentServicePointSloid())){
      throw new ReducedVariantException();
    }

    referencePointService.checkReferencePointExists(version.getReferencePointSloid(), "REFERENCE_POINT");

    isElementExisiting(version.getReferencePointElementType(), version.getSloid());

    return relationRepository.saveAndFlush(version);
  }

  private void isElementExisiting(ReferencePointElementType type, String sloid) {
    if(type == ReferencePointElementType.PLATFORM){
      platformService.checkPlatformExists(sloid, ReferencePointElementType.PLATFORM.name());
    }
    if(type == ReferencePointElementType.PARKING_LOT){
      parkingLotService.checkParkingLotExists(sloid, ReferencePointElementType.PARKING_LOT.name());
    }
    if(type == ReferencePointElementType.CONTACT_POINT){
      contactPointService.checkContactPointExists(sloid, ReferencePointElementType.CONTACT_POINT.name());
    }
    if(type == ReferencePointElementType.TOILET){
      toiletService.checkToiletExists(sloid, ReferencePointElementType.TOILET.name());
    }
  }
}
