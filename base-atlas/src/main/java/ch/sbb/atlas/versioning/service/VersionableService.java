package ch.sbb.atlas.versioning.service;

import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface VersionableService {

  <T extends Versionable> List<VersionedObject> versioningObjects(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  /**
   *
   * Used only for versioning when we do ImportFromCsv and not for ordinary ServicePointUpdate
   * @see ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1#importServicePoints(ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel)
   */
  <T extends Versionable> List<VersionedObject> versioningObjectsForImportFromCsv(Versionable current,
      Versionable edited,
      List<T> currentVersions);

  <T extends Versionable> void applyVersioning(Class<T> clazz,
      List<VersionedObject> versionedObjects, Consumer<T> save, LongConsumer deleteById);
}