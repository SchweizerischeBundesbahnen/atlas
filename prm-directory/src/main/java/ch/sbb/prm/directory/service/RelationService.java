package ch.sbb.prm.directory.service;

import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
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

  public RelationService(RelationRepository relationRepository, VersionableService versionableService,
      StopPointService stopPointService) {
    super(versionableService);
    this.relationRepository = relationRepository;
    this.stopPointService = stopPointService;
  }

  public List<RelationVersion> getRelationsBySloid(String sloid) {
    return relationRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  public List<RelationVersion> getRelationsByParentServicePointSloid(String parentServicePointSloid) {
    return relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
  }

  @Override
  protected void incrementVersion(String sloid) {
    relationRepository.incrementVersion(sloid);
  }

  @Override
  public RelationVersion save(RelationVersion version) {
    stopPointService.validateIsNotReduced(version.getParentServicePointSloid());
    initDefaultData(version);
    return relationRepository.saveAndFlush(version);
  }

  @Override
  public List<RelationVersion> getAllVersions(String sloid) {
    return relationRepository.findAllBySloid(sloid);
  }

  public List<RelationVersion> getAllVersionsBySloidAndReferencePoint(String sloid, String referencePointSloid) {
    return relationRepository.findBySloidAndReferencePointSloid(sloid, referencePointSloid);
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

  @Override
  public RelationVersion updateVersion(RelationVersion currentVersion, RelationVersion editedVersion) {
    editedVersion.setReferencePointSloid(currentVersion.getReferencePointSloid());

    List<RelationVersion> existingDbVersions = getAllVersionsBySloidAndReferencePoint(currentVersion.getSloid(),
        currentVersion.getReferencePointSloid());

    return updateVersion(currentVersion, editedVersion, existingDbVersions);
  }

  public Optional<RelationVersion> getRelationById(Long id) {
    return relationRepository.findById(id);
  }

  public Page<RelationVersion> findAll(RelationSearchRestrictions searchRestrictions) {
    return relationRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public RelationVersion createRelationThroughImport(RelationVersion version) {
    setStatusToValidate(version);
    return relationRepository.saveAndFlush(version);
  }
}
