package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.List;

public class VersionableServiceImpl implements VersionableService {

  private final VersioningEngine versioningEngine;

  public VersionableServiceImpl() {
    this.versioningEngine = new VersioningEngine();
  }

  @Override
  public List<VersionedObject> versioningObjects(Versionable actual, Versionable edited,
      List<AttributeObject> editedAttributeObjects,
      List<ToVersioning> objectsToVersioning) {

    List<VersionedObject> versionedObjects = versioningEngine.objectsVersioned(actual, edited,
        editedAttributeObjects, objectsToVersioning);
    return versionedObjects;
  }

  @Override
  public AttributeObject getAttributeObject(Long objectId, String fieldName, String value) {
    AttributeObject changedAttributeName;
    changedAttributeName = new AttributeObject(objectId, fieldName, value, "string");
    return changedAttributeName;
  }
}
