package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.RelationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RelationService extends PrmVersionableService<RelationVersion> {

  private final RelationRepository relationRepository;

  public RelationService(RelationRepository relationRepository, VersionableService versionableService) {
    super(versionableService);
    this.relationRepository = relationRepository;
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
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    relationRepository.incrementVersion(servicePointNumber);
  }

  @Override
  public RelationVersion save(RelationVersion relationVersion) {
    return relationRepository.saveAndFlush(relationVersion);
  }

  @Override
  protected List<RelationVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(RelationVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(relationRepository));
  }

  public RelationVersion updateRelationVersion(RelationVersion currentVersion, RelationVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<RelationVersion> getRelationById(Long id) {
    return relationRepository.findById(id);
  }

  public List<RelationVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return relationRepository.findAllByNumberOrderByValidFrom(number);
  }
}
