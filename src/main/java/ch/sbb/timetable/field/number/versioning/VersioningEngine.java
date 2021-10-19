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

    objectsToVersioning.sort(Comparator.comparing(o -> o.getVersionable().getValidFrom()));

    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null) {
      //get all versions between editedVersion.getValidFrom() and editedVersion.getValidTo()
    } else if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() == null) {
      //Only validFrom is edited
      //get all versions between editedVersion.getValidFrom() and actual.getValidTo()

      versionedObjects = getVersionsBetweenValidFromAndValidToWhenOnlyValidFromIsEdited(
          editedVersion, objectsToVersioning, changedAttributes);

    } else if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() != null) {
      //get all versions between actual.getValidFrom() and edited.getValidTo()
    }

    return versionedObjects;
  }

  private  List<VersionedObject> getVersionsBetweenValidFromAndValidToWhenOnlyValidFromIsEdited(
      Versionable editedVersion,
      List<ToVersioning> objectsToVersioning,
      List<AttributeObject> changedAttributes) {

    LocalDate validFrom = editedVersion.getValidFrom();
    //sort objectsToVersioning
    objectsToVersioning.sort(Comparator.comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //check if ValidFrom is Before the actualVersion. The versionedObjects is sorted, this means that
    //we have to check this condition on the first item
    ToVersioning firstItemObjectToVersioning = objectsToVersioning.get(0);
    if(validFrom.isBefore(firstItemObjectToVersioning.getVersionable().getValidFrom())){
      VersionedObject versionedObjectToUpdate =
          VersionedObject.builder()
                         .objectId(firstItemObjectToVersioning.getObjectId())
                         .validFrom(validFrom)
                         .validTo(editedVersion.getValidTo())
                         .attributeObjects(firstItemObjectToVersioning.getAttributeObjects())
                         .versionableObject(firstItemObjectToVersioning.getVersionable())
                         .action(VersioningAction.UPDATE)
                         .build();
      versionedObjects.add(versionedObjectToUpdate);
      return versionedObjects;
    }else{
      for(ToVersioning toVersioning : objectsToVersioning){
        log.info("ValidFrom: {} - ValidTo {}", toVersioning.getVersionable().getValidFrom(), toVersioning.getVersionable().getValidTo());
        if(validFrom.isEqual(toVersioning.getVersionable().getValidFrom())){
          //Should not here come because this means ValidFrom is not edited
        }
        else if(validFrom.isAfter(toVersioning.getVersionable().getValidFrom())){
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
