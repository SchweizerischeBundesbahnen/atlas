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
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RelationService {

  private final RelationRepository relationRepository;
  private final VersionableService versionableService;


  public List<RelationVersion> getRelationsBySloid(String sloid) {
   return relationRepository.findAllBySloid(sloid);
  }

  public List<RelationVersion> getRelationsBySloidAndReferenceType(String sloid, ReferencePointElementType referencePointType) {
   return relationRepository.findAllBySloidAndReferencePointElementType(sloid,referencePointType);
  }
  public List<RelationVersion> getRelationsByParentServicePointSloidAndReferenceType(String parentServicePointSloid,
      ReferencePointElementType referencePointType) {
   return relationRepository.findAllByParentServicePointSloidAndReferencePointElementType(parentServicePointSloid,referencePointType);
  }
  public List<RelationVersion> getRelationsByParentServicePointSloid(String parentServicePointSloid) {
   return relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
  }

  public void save(RelationVersion relationVersion){
    relationRepository.save(relationVersion);
  }

  public RelationVersion updateRelationVersion(RelationVersion currentVersion, RelationVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<RelationVersion> existingDbVersions = relationRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(RelationVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(relationRepository));
    return currentVersion;
  }

  private void checkStaleObjectIntegrity(RelationVersion currentVersion, RelationVersion editedVersion) {
    relationRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(RelationVersion.class.getSimpleName(), "version");
    }
  }

  public Optional<RelationVersion> getRelationById(Long id) {
    return relationRepository.findById(id);
  }

  public List<RelationVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return relationRepository.findAllByNumberOrderByValidFrom(number);
  }
}
