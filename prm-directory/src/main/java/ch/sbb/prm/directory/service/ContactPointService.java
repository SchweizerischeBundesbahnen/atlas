package ch.sbb.prm.directory.service;


import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContactPointService extends PrmRelatableVersionableService<ContactPointVersion> {

  private final ContactPointRepository contactPointRepository;

  public ContactPointService(ContactPointRepository contactPointRepository, StopPointService stopPointService,
                             RelationService relationRepository, ReferencePointRepository referencePointRepository,
                             VersionableService versionableService) {
    super(versionableService, stopPointService, relationRepository, referencePointRepository);
    this.contactPointRepository = contactPointRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return ReferencePointElementType.CONTACT_POINT;
  }

  @Override
  protected void incrementVersion(String sloid) {
    contactPointRepository.incrementVersion(sloid);
  }

  @Override
  protected ContactPointVersion save(ContactPointVersion version) {
    return contactPointRepository.saveAndFlush(version);
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

  public List<ContactPointVersion> getAllContactPoints() {
    return contactPointRepository.findAll();
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public ContactPointVersion createContactPoint(ContactPointVersion version) {
    createRelation(version);
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

}
