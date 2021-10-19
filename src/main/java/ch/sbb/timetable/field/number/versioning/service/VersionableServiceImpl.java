package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionableServiceImpl implements VersionableService {

  private final VersioningEngine versioningEngine;

  public VersionableServiceImpl() {
    this.versioningEngine = new VersioningEngine();
  }

  @Override
  public List<VersionedObject> versioningObjects(Versionable actual, Versionable edited,
      List<AttributeObject> editedAttributeObjects,
      List<ToVersioning> objectsToVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //compare if Versionable date from and/or to are changed
    if (edited.getValidTo() == null && edited.getValidFrom() == null) {
      //update actual version
      List<AttributeObject> collectedActualAttributesObject =
          editedAttributeObjects
              .stream()
              .filter(attributeObject -> attributeObject.getObjectId() == actual.getId())
              .collect(Collectors.toList());
      versionedObjects.add(
          VersionedObject.builder()
                         .objectId(actual.getId())
                         .versionableObject(actual)
                         .attributeObjects(collectedActualAttributesObject)
                         .action(VersioningAction.UPDATE)
                         .build()
      );
    } else {
      versionedObjects = versioningEngine.objectsVersioned(actual, edited,
          editedAttributeObjects, objectsToVersioning);
    }
    return versionedObjects;
  }

  @Override
  public AttributeObject getAttributeObject(Long objectId, String fieldName, String value) {
    AttributeObject changedAttributeName;
    changedAttributeName = new AttributeObject(objectId, fieldName, value, "string");
    return changedAttributeName;
  }
}
