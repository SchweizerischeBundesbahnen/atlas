package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.RelationRepository;
import java.util.List;
import java.util.Optional;
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

}
