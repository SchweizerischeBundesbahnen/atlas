package ch.sbb.timetable.field.number.versioning;

import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VersioningEngine {

  public List<VersionedObject> objectsVersioned(Versionable actualVersion,
      Versionable editedVersion,
      List<AttributeObject> changedAttributes,
      List<ToVersioning> objectsToVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    objectsToVersioning.sort(Comparator.comparing(o -> o.getVersionable().getValidFrom()));

    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null) {
      //get all versions between editedVersion.getValidFrom() and editedVersion.getValidTo()
    } else if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() == null) {
      //get all versions between editedVersion.getValidFrom() and actual.getValidTo()
      List<ToVersioning> toVersioningInDateRange =
          objectsToVersioning
              .stream()
              .filter(toVersioning ->
                  toVersioning
                      .getVersionable()
                      .getValidFrom().isEqual(editedVersion.getValidFrom())
                      ||
                      toVersioning
                          .getVersionable()
                          .getValidFrom().isAfter(editedVersion.getValidFrom()))
              .collect(Collectors.toList());
      if (toVersioningInDateRange.size() == 1) {
        //Update actual Version
        ToVersioning toVersioning = toVersioningInDateRange.get(0);

        //Get actual validTo
        LocalDate actualValidTo = toVersioning.getVersionable().getValidTo();

        //create VersionedObject UPDATE
        toVersioning.getVersionable().setValidTo(editedVersion.getValidFrom());

        VersionedObject updatedVersion =
            VersionedObject.builder()
                           .objectId(toVersioning.getObjectId())
                           .attributeObjects(toVersioning.getAttributeObjects())
                           .versionableObject(toVersioning.getVersionable())
                           .action(VersioningAction.UPDATE)
                           .build();
        versionedObjects.add(updatedVersion);

        //Create VersionObject NEW
        toVersioning.getVersionable().setValidFrom(editedVersion.getValidFrom().plusDays(1));
        toVersioning.getVersionable().setValidTo(actualValidTo.plusDays(1));

        VersionedObject newVersion =
            VersionedObject.builder()
                           .objectId(null)
                           .versionableObject(toVersioning.getVersionable())
                           .attributeObjects(
                               replaceChangedAttributeWithActualAttribute(changedAttributes,
                                   toVersioning.getAttributeObjects())
                           )
                           .action(VersioningAction.NEW)
                           .build();
        versionedObjects.add(newVersion);
      }

    } else if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() != null) {
      //get all versions between actual.getValidFrom() and edited.getValidTo()
    }

    return versionedObjects;
  }

  public List<AttributeObject> replaceChangedAttributeWithActualAttribute(
      List<AttributeObject> changedAttributes, List<AttributeObject> actualAttributes) {
    List<AttributeObject> attributeObjects = new ArrayList<>();

    for (AttributeObject changedAttributeObject : changedAttributes) {
      for (AttributeObject actualAttributeObject : actualAttributes) {
        if (changedAttributeObject.getKey().equals(actualAttributeObject.getKey())) {
          AttributeObject modifiedAttributeObject = buildModifiedAttributeObject(
              changedAttributeObject,
              actualAttributeObject);
          attributeObjects.add(modifiedAttributeObject);
        } else {
          attributeObjects.add(actualAttributeObject);
        }
      }
    }
    return attributeObjects;

  }

  private AttributeObject buildModifiedAttributeObject(AttributeObject changedAttributeObject,
      AttributeObject actualAttributeObject) {
    return AttributeObject
        .builder()
        .objectId(actualAttributeObject.getObjectId())
        .key(actualAttributeObject.getKey())
        .value(changedAttributeObject.getValue())
        .type(actualAttributeObject.getType())
        .build();
  }

}
