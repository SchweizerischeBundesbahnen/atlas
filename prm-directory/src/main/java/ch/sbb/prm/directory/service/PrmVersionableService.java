package ch.sbb.prm.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.RelationVersion;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;

@Slf4j
@RequiredArgsConstructor
public abstract class PrmVersionableService<T extends PrmVersionable> {

  protected final VersionableService versionableService;

  protected abstract void incrementVersion(String sloid);

  protected abstract T save(T version);

  public abstract List<T> getAllVersions(String sloid);

  protected abstract void applyVersioning(List<VersionedObject> versionedObjects);

  public T updateVersion(T currentVersion, T editedVersion) {
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<T> existingDbVersions = getAllVersions(currentVersion.getSloid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    applyVersioning(versionedObjects);
    return currentVersion;
  }

  protected void checkStaleObjectIntegrity(T currentVersion, T editedVersion) {
    incrementVersion(currentVersion.getSloid());
    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(RelationVersion.class.getSimpleName(), "version");
    }
  }
}
