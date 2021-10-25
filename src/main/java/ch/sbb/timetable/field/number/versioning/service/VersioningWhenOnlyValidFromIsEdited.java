package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.model.Entity;
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
public class VersioningWhenOnlyValidFromIsEdited extends Versioning{

  @Override
  public List<VersionedObject> applyVersioning(
      Versionable editedVersion,
      Versionable currentVersion,
      List<ToVersioning> objectsToVersioning,
      Entity editedEntity) {

    LocalDate validFrom = editedVersion.getValidFrom();
    //sort objectsToVersioning
    objectsToVersioning.sort(
        Comparator.comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //check if ValidFrom is Before the currentVersion. The versionedObjects is sorted, this means that
    //we have to check this condition on the first item
    ToVersioning firstItemObjectToVersioning = objectsToVersioning.get(0);
    if (validFrom.isBefore(firstItemObjectToVersioning.getVersionable().getValidFrom())) {
      //duplicate
      ToVersioning toVersioning = objectsToVersioning
          .stream()
          .filter(versioning -> versioning.getEntity().getId().equals(currentVersion.getId()))
          .findFirst()
          .orElse(null);

      VersionedObject versionedObjectToUpdate =
          buildObjectToUpdate(currentVersion, editedEntity, validFrom, firstItemObjectToVersioning,
              toVersioning);
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
                             .validFrom(toVersioning.getVersionable().getValidFrom())
                             .validTo(validFrom.minusDays(1))
                             .entity(toVersioning.getEntity())
                             .action(VersioningAction.UPDATE)
                             .build();
          versionedObjects.add(updatedVersion);
          //Create VersionObject NEW
          VersionedObject newVersion =
              VersionedObject.builder()
                             .validFrom(editedVersion.getValidFrom())
                             .validTo(toVersioning.getVersionable().getValidTo())
                             .entity(
                                 replaceChangedAttributeWithActualAttribute(null, editedEntity,
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

  private VersionedObject buildObjectToUpdate(Versionable currentVersion, Entity editedEntity,
      LocalDate validFrom, ToVersioning firstItemObjectToVersioning, ToVersioning toVersioning) {
    return VersionedObject.builder()
                   .validFrom(validFrom)
                   .validTo(firstItemObjectToVersioning.getVersionable().getValidTo())
                   .entity(firstItemObjectToVersioning.getEntity())
                   .entity(
                       replaceChangedAttributeWithActualAttribute(currentVersion.getId(),
                           editedEntity,
                           toVersioning.getEntity())
                   )
                   .action(VersioningAction.UPDATE)
                   .build();
  }
}
