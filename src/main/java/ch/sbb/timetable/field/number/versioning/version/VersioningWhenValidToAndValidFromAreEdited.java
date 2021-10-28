package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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

    List<ToVersioning> objectToVersioningInValidFromValidToRange = findObjectToVersioningInValidFromValidToRange(
        objectsToVersioning, editedValidFrom, editedValidTo);

    if (objectToVersioningInValidFromValidToRange.size() == 1) {
      ToVersioning toVersioning = objectToVersioningInValidFromValidToRange.get(0);
      // Scenario2:
      // The edited version is in the middle of an existing Version
      if (editedValidFrom.isAfter(toVersioning.getVersionable().getValidFrom())
          && editedValidTo.isBefore(toVersioning.getVersionable().getValidTo())) {
        System.out.println("Found in the middle of an exiting Version -> Scenario 2");

        //1. Update the existing version:
        // currentVersion.setValidTo(editedValidFrom.minusDay(1))
        // do not change properties
        LocalDate toUpdateValidTo = editedValidFrom.minusDays(1);
        VersionedObject toUpdateVersionedObject = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(), toUpdateValidTo,
            toVersioning.getEntity());
        versionedObjects.add(toUpdateVersionedObject);
        //2. Copy the currentVersion splittedCurrentVersion.copy(currentVersion)
        //  splittedCurrentVersion.setValidFrom(editedValidTo.plusDay(1))
        //  splittedCurrentVersion.setValidTo(currentVersion.ValidTo())
        // do not change properties
        LocalDate toAddAtEndValidFrom = editedValidTo.plusDays(1);
        LocalDate toAddAtEndValidTo = currentVersion.getValidTo();
        VersionedObject toAddAtEndVersionedObject = buildVersionedObjectToCreate(
            toAddAtEndValidFrom,
            toAddAtEndValidTo, toVersioning.getEntity());
        versionedObjects.add(toAddAtEndVersionedObject);
        //3. Create a new Version
        // newVersion.setValidFrom(edited.validFrom())
        // newVersion.setValidTo(edited.validTo())
        // copy all properties from currentVersion and replace them with the edited properties
        Entity entityToAddAtEnd = replaceEditedPropertiesWithCurrentProperties(editedEntity,
            toVersioning.getEntity());
        VersionedObject toCreateVersionedObject = buildVersionedObjectToCreate(editedValidFrom,
            editedValidTo, entityToAddAtEnd);
        versionedObjects.add(toCreateVersionedObject);
      }
    } else {
      //Found more than one versions
      // 1. versions.get(0)
      //    validTo = editedValidFrom.minusDay(1)
      //    do not update properties
      //    VersioningAction = UPDATE
      // 2. versions.get(versions.size()-1)
      //    validFrom = editedValidTo.plusDay(1)
      //    do not update properties
      //    VersioningAction = UPDATE
      // 3. versions.get(n+1)
      //    TODO
    }

    return versionedObjects;
  }

  private List<ToVersioning> findObjectToVersioningInValidFromValidToRange(
      List<ToVersioning> objectsToVersioning,
      LocalDate editedValidFrom, LocalDate editedValidTo) {
    return objectsToVersioning.stream()
                              .filter(
                                  toVersioning -> !toVersioning.getVersionable()
                                                               .getValidFrom()
                                                               .isAfter(
                                                                   editedValidTo))
                              .filter(
                                  toVersioning -> !toVersioning.getVersionable()
                                                               .getValidTo()
                                                               .isBefore(
                                                                   editedValidFrom))
                              .collect(
                                  Collectors.toList());
  }
}
