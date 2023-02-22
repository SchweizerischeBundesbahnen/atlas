package ch.sbb.atlas.base.service.versioning.service;

import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface VersionableService {

  <T extends Versionable> List<VersionedObject> versioningObjects(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  <T extends Versionable> List<VersionedObject> versioningObjectsWithDeleteByNullProperties(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  <T extends Versionable> void applyVersioning(Class<T> clazz,
      List<VersionedObject> versionedObjects, Consumer<T> save, LongConsumer deleteById);
}