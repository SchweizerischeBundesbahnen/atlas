package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.mapper.ContactPointVersionMapper;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.search.ContactPointSearchRestrictions;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ContactPointService extends PrmRelatableVersionableService<ContactPointVersion> {

  private final ContactPointRepository contactPointRepository;

  public ContactPointService(ContactPointRepository contactPointRepository, StopPointService stopPointService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService, PrmLocationService locationService) {
    super(versionableService, stopPointService, relationRepository, referencePointRepository, locationService);
    this.contactPointRepository = contactPointRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return ReferencePointElementType.CONTACT_POINT;
  }

  @Override
  protected SloidType getSloidType() {
    return SloidType.CONTACT_POINT;
  }

  @Override
  protected void incrementVersion(String sloid) {
    contactPointRepository.incrementVersion(sloid);
  }

  @Override
  protected ContactPointVersion save(ContactPointVersion version) {
    initDefaultData(version);
    return contactPointRepository.saveAndFlush(version);
  }

  public Page<ContactPointVersion> findAll(ContactPointSearchRestrictions searchRestrictions) {
    return contactPointRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  @Override
  public List<ContactPointVersion> getAllVersions(String sloid) {
    return contactPointRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ContactPointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(contactPointRepository));
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public ContactPointVersion createContactPoint(ContactPointVersion version) {
    createRelationWithSloidAllocation(version);
    return save(version);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ContactPointVersion updateContactPointVersion(ContactPointVersion currentVersion,
      ContactPointVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ContactPointVersion> getContactPointVersionById(Long id) {
    return contactPointRepository.findById(id);
  }

  public ContactPointVersion createContactPointThroughImport(ContactPointVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    locationService.allocateSloid(version, SloidType.CONTACT_POINT);
    setStatusToValidate(version);
    return contactPointRepository.saveAndFlush(version);
  }

  public List<ContactPointVersion> findByParentServicePointSloid(String parentServicePointSloid) {
    return contactPointRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public List<ContactPointOverviewModel> buildOverview(List<ContactPointVersion> parkingLotVersions) {
    List<ContactPointVersion> mergedVersions = OverviewDisplayBuilder.mergeVersionsForDisplay(parkingLotVersions,
        ContactPointVersion::getSloid);
    return mergedVersions.stream()
        .map(contactPoint -> ContactPointVersionMapper.toOverviewModel(contactPoint,
            getRecordingStatusIncludingRelation(contactPoint.getSloid(), contactPoint.getRecordingStatus())))
        .toList();
  }

  public void checkContactPointExists(String sloid, String type) {
    if (!contactPointRepository.existsBySloid(sloid)) {
      throw new ElementTypeDoesNotExistException(sloid, type);
    }
  }

}
