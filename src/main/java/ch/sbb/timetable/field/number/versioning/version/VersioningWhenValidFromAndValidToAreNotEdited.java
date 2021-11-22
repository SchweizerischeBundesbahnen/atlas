package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.fillNotTouchedVersionedObject;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.List;

/**
 * Scenario 1q,1b,1c
 */
public class VersioningWhenValidFromAndValidToAreNotEdited extends Versioning {

  @Override
  public List<VersionedObject> applyVersioning(Versionable currentVersion, Entity editedEntity,
      List<ToVersioning> objectsToVersioning) {
    ToVersioning toVersioning = findObjectToVersioning(currentVersion, objectsToVersioning);
    List<VersionedObject> versionedObjects = fillNotTouchedVersionedObject(objectsToVersioning,List.of(toVersioning));

    Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
        toVersioning.getEntity());

    versionedObjects.add(buildVersionedObjectToUpdate(currentVersion.getValidFrom(),
        currentVersion.getValidTo(), entity));
    return versionedObjects;
  }
}
