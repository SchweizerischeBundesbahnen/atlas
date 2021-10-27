package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenOnlyValidFromIsEdited extends Versioning {

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
      ToVersioning toVersioning = findObjectToVersioning(currentVersion, objectsToVersioning);
      Entity entity = replaceChangedAttributeWithActualAttribute(currentVersion.getId(),
          editedEntity,
          toVersioning.getEntity());
      VersionedObject versionedObjectToUpdate = buildVersionedObjectToUpdate(validFrom,
          firstItemObjectToVersioning.getVersionable().getValidTo(), entity);
      versionedObjects.add(versionedObjectToUpdate);
      return versionedObjects;
    } else {
      for (ToVersioning toVersioning : objectsToVersioning) {
        log.info("ValidFrom: {} - ValidTo {}", toVersioning.getVersionable().getValidFrom(),
            toVersioning.getVersionable().getValidTo());
        if (validFrom.isEqual(toVersioning.getVersionable().getValidFrom())) {
          throw new IllegalStateException("Should not here come because this means ValidFrom is not edited");
        } else if (validFrom.isAfter(toVersioning.getVersionable().getValidFrom())) {
          //1. we need to
          //   a. add a new Version after the actual Version
          //   b. update the actual Version validTo = validFrom.minusDays(1)
          VersionedObject updatedVersion = buildVersionedObjectToUpdate(
              toVersioning.getVersionable().getValidFrom(), validFrom.minusDays(1),
              toVersioning.getEntity());
          versionedObjects.add(updatedVersion);
          //Create VersionObject NEW
          VersionedObject newVersion = createNewVersion(
              editedVersion, editedEntity, toVersioning);
          versionedObjects.add(newVersion);
          return versionedObjects;
        }
      }
    }

    return versionedObjects;
  }

  private VersionedObject createNewVersion(Versionable editedVersion, Entity editedEntity,
      ToVersioning toVersioning) {
    Entity entity = replaceChangedAttributeWithActualAttribute(null, editedEntity,
        toVersioning.getEntity());

    VersionedObject newVersion = buildVersionedObjectToCreate(editedVersion.getValidFrom(),
        toVersioning.getVersionable().getValidTo(), entity);
    return newVersion;
  }
}
