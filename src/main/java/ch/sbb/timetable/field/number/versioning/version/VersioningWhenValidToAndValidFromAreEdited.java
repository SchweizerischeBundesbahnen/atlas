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
public class VersioningWhenValidToAndValidFromAreEdited extends Versioning {

  @Override
  public List<VersionedObject> applyVersioning(
      Versionable editedVersion,
      Versionable currentVersion,
      List<ToVersioning> objectsToVersioning,
      Entity editedEntity) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    LocalDate editedValidFrom = editedVersion.getValidFrom();
    LocalDate editedValidTo = editedVersion.getValidTo();

    //sort objectsToVersioning
    objectsToVersioning.sort(
        Comparator.comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));

    for (ToVersioning toVersioning : objectsToVersioning) {
      // Scenario2:
      // The edited version is in the middle of an existing Version
      if (editedValidFrom.isAfter(toVersioning.getVersionable().getValidFrom()) && editedValidTo.isBefore(toVersioning.getVersionable().getValidTo())){
        System.out.println("Found in the middle of an exiting Version -> Scenario 2");

        //1. Update the existing version:
        // currentVersion.setValidTo(editedValidFrom.minusDay(1))
        // do not change properties
        LocalDate toUpdateValidTo = editedValidFrom.minusDays(1);
        VersionedObject toUpdateVersionedObject = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(), toUpdateValidTo, toVersioning.getEntity());
        versionedObjects.add(toUpdateVersionedObject);
        //2. Copy the currentVersion splittedCurrentVersion.copy(currentVersion)
        //  splittedCurrentVersion.setValidFrom(editedValidTo.plusDay(1))
        //  splittedCurrentVersion.setValidTo(currentVersion.ValidTo())
        // do not change properties
        LocalDate toAddAtEndValidFrom = editedValidTo.plusDays(1);
        LocalDate toAddAtEndValidTo = currentVersion.getValidTo();
        VersionedObject toAddAtEndVersionedObject = buildVersionedObjectToCreate(toAddAtEndValidFrom,
            toAddAtEndValidTo, toVersioning.getEntity());
        versionedObjects.add(toAddAtEndVersionedObject);
        //3. Create a new Version
        // newVersion.setValidFrom(edited.validFrom())
        // newVersion.setValidTo(edited.validTo())
        // copy all properties from currentVersion and replace them with the edited properties
        Entity entityToAddAtEnd = replaceChangedAttributeWithActualAttribute(null,
            editedEntity,
            toVersioning.getEntity());
        VersionedObject toCreateVersionedObject = buildVersionedObjectToCreate(editedValidFrom,
            editedValidTo, entityToAddAtEnd);
        versionedObjects.add(toCreateVersionedObject);
      }
    }

    return versionedObjects;
  }
}
