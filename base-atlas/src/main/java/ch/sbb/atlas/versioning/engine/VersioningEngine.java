package ch.sbb.atlas.versioning.engine;

import ch.sbb.atlas.versioning.convert.ConverterHelper;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import ch.sbb.atlas.versioning.merge.MergeHelper;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import ch.sbb.atlas.versioning.version.Versioning;
import ch.sbb.atlas.versioning.version.VersioningHelper;
import ch.sbb.atlas.versioning.version.VersioningWhenValidFromAndValidToAreNotEdited;
import ch.sbb.atlas.versioning.version.VersioningWhenValidToAndOrValidFromAreEdited;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public <T extends Versionable> List<VersionedObject> applyVersioning(boolean deletePropertyWhenNull,
      Versionable currentVersion, Versionable editedVersion,
      List<T> currentVersions, List<VersionableProperty> versionableProperties) {

    Entity editedEntity = ConverterHelper.convertToEditedEntity(deletePropertyWhenNull, currentVersion, editedVersion,
        versionableProperties);

    List<ToVersioning> objectsToVersioning = ConverterHelper.convertAllObjectsToVersioning(
        currentVersions, versionableProperties);

    VersioningData vd = new VersioningData(editedVersion, currentVersion, editedEntity,
        objectsToVersioning);

    Versioning versioning;
    if (VersioningHelper.areValidToAndValidFromNotEdited(editedVersion, currentVersion)) {
      log.info("ValidFrom and ValidTo are not edited.");
      versioning = new VersioningWhenValidFromAndValidToAreNotEdited();
    } else {
      log.info("ValidFrom and/or ValidTo are edited.");
      versioning = new VersioningWhenValidToAndOrValidFromAreEdited();
    }
    List<VersionedObject> versionedObjects = new ArrayList<>(versioning.applyVersioning(vd));
    List<VersionedObject> mergedVersionedObjects = MergeHelper.mergeVersionedObject(
        versionedObjects);

    boolean hasChanges = VersioningHelper.checkChangesAfterVersioning(vd, mergedVersionedObjects);
    if (!hasChanges) {
      log.info("No changes made after performing versioning.");
      throw new VersioningNoChangesException();
    }
    return mergedVersionedObjects;
  }

}
