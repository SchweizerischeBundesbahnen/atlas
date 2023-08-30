package ch.sbb.atlas.versioning.service;

import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface VersionableService {

  /**
   * Patch Versioning.
   * Null properties will be treated as not to update.
   */
  <T extends Versionable> List<VersionedObject> versioningObjects(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  /**
   * Properties set to null will get Deleted from the DB
   */
  <T extends Versionable> List<VersionedObject> versioningObjectsDeletingNullProperties(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  <T extends Versionable> void applyVersioning(Class<T> clazz,
      List<VersionedObject> versionedObjects, Consumer<T> save, LongConsumer deleteById);
}
