package ch.sbb.timetable.field.number.versioning;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public List<VersionedObject> applyVersioning(Versionable currentVersion,
      Versionable editedVersion,
      Entity editedEntity,
      List<ToVersioning> objectsToVersioning) {

    List<VersionedObject> versionedObjects = new ArrayList<>();

    //Temporal sort objects versioning
    objectsToVersioning.sort(Comparator.comparing(o -> o.getVersionable().getValidFrom()));

    //validFrom and validTo are not modified
    if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) {
      //update actual version
      VersionedObject versionedObjectToUpdate = getVersionedObjectWhenValidFromAndValidToAreNotModified(
          currentVersion, editedEntity, objectsToVersioning);
      versionedObjects.add(versionedObjectToUpdate);
      return versionedObjects;
    }

    //only validFrom is modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() == null) {
      //Only validFrom is edited
      //get all versions between editedVersion.getValidFrom() and actual.getValidTo()
      versionedObjects = getVersionedObjectWhenOnlyValidFromIsEdited(
          editedVersion, currentVersion, objectsToVersioning, editedEntity);
      return versionedObjects;
    }

    //only validTo is modified
    if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() != null) {
      //get all versions between actual.getValidFrom() and edited.getValidTo()
      versionedObjects = getVersionedObjectWhenOnlyValidToIsEdited(
          editedVersion, objectsToVersioning, editedEntity);
      return versionedObjects;
    }

    //validFrom and validTo are modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null) {
      //get all versions between editedVersion.getValidFrom() and editedVersion.getValidTo()
      versionedObjects = getVersionedObjectWhenValidFromAndValidToAreModified(
          editedVersion, objectsToVersioning, editedEntity);
      return versionedObjects;
    }

    return versionedObjects;
  }

  private List<VersionedObject> getVersionedObjectWhenValidFromAndValidToAreModified(
      Versionable editedVersion, List<ToVersioning> objectsToVersioning,
      Entity editedEntity) {
    return null;
  }

  private List<VersionedObject> getVersionedObjectWhenOnlyValidToIsEdited(Versionable editedVersion,
      List<ToVersioning> objectsToVersioning, Entity editedEntity) {
    return null;
  }

  private VersionedObject getVersionedObjectWhenValidFromAndValidToAreNotModified(
      Versionable current,
      Entity editedEntity, List<ToVersioning> objectsToVersioning) {

    //duplicate
    ToVersioning toVersioning = objectsToVersioning
        .stream()
        .filter(versioning -> versioning.getObjectId().equals(current.getId()))
        .findFirst()
        .orElse(null);

    VersionedObject versionedObjectToUpdate =
        VersionedObject.builder()
                       .objectId(current.getId())
                       .validFrom(current.getValidFrom())
                       .validTo(current.getValidTo())
                       .entity(
                           replaceChangedAttributeWithActualAttribute(current.getId(),editedEntity,
                               toVersioning.getEntity())
                       )
                       .action(VersioningAction.UPDATE)
                       .build();
    return versionedObjectToUpdate;
  }

  private List<VersionedObject> getVersionedObjectWhenOnlyValidFromIsEdited(
      Versionable editedVersion,
      Versionable actualVersion,
      List<ToVersioning> objectsToVersioning,
      Entity editedEntity) {

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
                         .entity(firstItemObjectToVersioning.getEntity())
                         .entity(
                             replaceChangedAttributeWithActualAttribute(null, editedEntity,
                                 toVersioning.getEntity())
                         )
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
                             .entity(toVersioning.getEntity())
                             .action(VersioningAction.UPDATE)
                             .build();
          versionedObjects.add(updatedVersion);
          //Create VersionObject NEW
          VersionedObject newVersion =
              VersionedObject.builder()
                             .objectId(null)
                             .validFrom(editedVersion.getValidFrom())
                             .validTo(toVersioning.getVersionable().getValidTo())
                             .entity(
                                 replaceChangedAttributeWithActualAttribute(null,editedEntity,
                                     toVersioning.getEntity())
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

  public Entity replaceChangedAttributeWithActualAttribute(Long objectId,
      Entity editedEntity, Entity currentEntity) {

    //Copy currentEntity
    List<Property> properties = new ArrayList<>(currentEntity.getProperties());

    for (Property editedProperty : editedEntity.getProperties()) {
      //find the index of the edited attribute end replace it with the new value
      int index = IntStream.range(0, properties.size())
                           .filter(i -> currentEntity.getProperties().get(i)
                                                        .getKey()
                                                        .equals(editedProperty.getKey()))
                           .findFirst().orElse(-1);
      if (index >= 0) {
        Property replacedProperty = replaceProperty(editedProperty,
            properties.get(index));
        properties.set(index, replacedProperty);
      }

    }
    Entity entity = Entity.builder()
                          .id(objectId)
                          .properties(properties)
                          .build();
    return entity;
  }

  private Property replaceProperty(Property editedProperty,
      Property currentProperty) {
    return Property
        .builder()
        .key(currentProperty.getKey())
        .value(editedProperty.getValue())
        .build();
  }

}
