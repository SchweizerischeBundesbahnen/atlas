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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public List<VersionedObject> objectsVersioned(Versionable actualVersion,
      Versionable editedVersion,
      List<AttributeObject> changedAttributes,
      List<ToVersioning> objectsToVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    //Temporal sort objects versioning
    objectsToVersioning.sort(Comparator.comparing(o -> o.getVersionable().getValidFrom()));

    //validFrom and validTo are not modified
    if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) {
      //update actual version
      VersionedObject versionedObjectToUpdate = getVersionedObjectWhenValidFromAndValidToAreNotModified(
          actualVersion, changedAttributes, objectsToVersioning);
      versionedObjects.add(versionedObjectToUpdate);
      return versionedObjects;
    }

    //only validFrom is modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() == null) {
      //Only validFrom is edited
      //get all versions between editedVersion.getValidFrom() and actual.getValidTo()
      versionedObjects = getVersionedObjectWhenOnlyValidFromIsEdited(
          editedVersion, actualVersion, objectsToVersioning, changedAttributes);
      return versionedObjects;
    }

    //only validTo is modified
    if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() != null) {
      //get all versions between actual.getValidFrom() and edited.getValidTo()
      versionedObjects = getVersionedObjectWhenOnlyValidToIsEdited(
          editedVersion, objectsToVersioning, changedAttributes);
      return versionedObjects;
    }

    //validFrom and validTo are modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null) {
      //get all versions between editedVersion.getValidFrom() and editedVersion.getValidTo()
      versionedObjects = getVersionedObjectWhenValidFromAndValidToAreModified(
          editedVersion, objectsToVersioning, changedAttributes);
      return versionedObjects;
    }

    return versionedObjects;
  }

  private List<VersionedObject> getVersionedObjectWhenValidFromAndValidToAreModified(
      Versionable editedVersion, List<ToVersioning> objectsToVersioning,
      List<AttributeObject> changedAttributes) {
    return null;
  }

  private List<VersionedObject> getVersionedObjectWhenOnlyValidToIsEdited(Versionable editedVersion,
      List<ToVersioning> objectsToVersioning, List<AttributeObject> changedAttributes) {
    return null;
  }

  private VersionedObject getVersionedObjectWhenValidFromAndValidToAreNotModified(
      Versionable actualVersion,
      List<AttributeObject> changedAttributes, List<ToVersioning> objectsToVersioning) {

    //duplicate
    ToVersioning toVersioning = objectsToVersioning
        .stream()
        .filter(versioning -> versioning.getObjectId().equals(actualVersion.getId()))
        .findFirst()
        .orElse(null);

    VersionedObject versionedObjectToUpdate =
        VersionedObject.builder()
                       .objectId(actualVersion.getId())
                       .validFrom(actualVersion.getValidFrom())
                       .validTo(actualVersion.getValidTo())
                       .attributeObjects(
                           replaceChangedAttributeWithActualAttribute(changedAttributes,
                               toVersioning.getAttributeObjects())
                       )
                       .versionableObject(actualVersion)
                       .action(VersioningAction.UPDATE)
                       .build();
    return versionedObjectToUpdate;
  }

  private List<VersionedObject> getVersionedObjectWhenOnlyValidFromIsEdited(
      Versionable editedVersion,
      Versionable actualVersion,
      List<ToVersioning> objectsToVersioning,
      List<AttributeObject> changedAttributes) {

    LocalDate validFrom = editedVersion.getValidFrom();
    //sort objectsToVersioning
    objectsToVersioning.sort(
        Comparator.comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //check if ValidFrom is Before the actualVersion. The versionedObjects is sorted, this means that
    //we have to check this condition on the first item
    ToVersioning firstItemObjectToVersioning = objectsToVersioning.get(0);
    if (validFrom.isBefore(firstItemObjectToVersioning.getVersionable().getValidFrom())) {
      //duplicate
      ToVersioning toVersioning = objectsToVersioning
          .stream()
          .filter(versioning -> versioning.getObjectId().equals(actualVersion.getId()))
          .findFirst()
          .orElse(null);

      VersionedObject versionedObjectToUpdate =
          VersionedObject.builder()
                         .objectId(firstItemObjectToVersioning.getObjectId())
                         .validFrom(validFrom)
                         .validTo(firstItemObjectToVersioning.getVersionable().getValidTo())
                         .attributeObjects(firstItemObjectToVersioning.getAttributeObjects())
                         .attributeObjects(
                             replaceChangedAttributeWithActualAttribute(changedAttributes,
                                 toVersioning.getAttributeObjects())
                         )
                         .versionableObject(firstItemObjectToVersioning.getVersionable())
                         .action(VersioningAction.UPDATE)
                         .build();
      versionedObjects.add(versionedObjectToUpdate);
      return versionedObjects;
    } else {
      for (ToVersioning toVersioning : objectsToVersioning) {
        log.info("ValidFrom: {} - ValidTo {}", toVersioning.getVersionable().getValidFrom(),
            toVersioning.getVersionable().getValidTo());
        if (validFrom.isEqual(toVersioning.getVersionable().getValidFrom())) {
          //Should not here come because this means ValidFrom is not edited
        } else if (validFrom.isAfter(toVersioning.getVersionable().getValidFrom())) {
          //1. we need to
          //   a. add a new Version after the actual Version
          //   b. update the actual Version validTo = validFrom.minusDays(1)
          VersionedObject updatedVersion =
              VersionedObject.builder()
                             .objectId(toVersioning.getObjectId())
                             .validFrom(toVersioning.getVersionable().getValidFrom())
                             .validTo(validFrom.minusDays(1))
                             .attributeObjects(toVersioning.getAttributeObjects())
                             .versionableObject(toVersioning.getVersionable())
                             .action(VersioningAction.UPDATE)
                             .build();
          versionedObjects.add(updatedVersion);
          //Create VersionObject NEW
          VersionedObject newVersion =
              VersionedObject.builder()
                             .objectId(null)
                             .versionableObject(toVersioning.getVersionable())
                             .validFrom(editedVersion.getValidFrom())
                             .validTo(toVersioning.getVersionable().getValidTo())
                             .attributeObjects(
                                 replaceChangedAttributeWithActualAttribute(changedAttributes,
                                     toVersioning.getAttributeObjects())
                             )
                             .action(VersioningAction.NEW)
                             .build();
          versionedObjects.add(newVersion);
          return versionedObjects;
        }
      }
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
