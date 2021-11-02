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
      List<VersionedObject> versionedObjectsInTheMiddleOfAnExistingEntity = applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
          currentVersion, editedEntity, editedValidFrom, editedValidTo,
          objectToVersioningInValidFromValidToRange);
      versionedObjects.addAll(versionedObjectsInTheMiddleOfAnExistingEntity);
    } else {
      List<VersionedObject> versionedObjectsOverMultipleEntity = applyVersioningOverMultipleEntity(editedEntity,
          editedValidFrom, editedValidTo,
          objectToVersioningInValidFromValidToRange);
      versionedObjects.addAll(versionedObjectsOverMultipleEntity);
    }

    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningOverMultipleEntity(Entity editedEntity,
      LocalDate editedValidFrom, LocalDate editedValidTo,
      List<ToVersioning> objectToVersioningInValidFromValidToRange) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //applyVersioningOverMultipleObjects
    //Found more than one versions
    // 1. versions.get(0)
    //    validTo = editedValidFrom.minusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE
    ToVersioning index0toVersioning = objectToVersioningInValidFromValidToRange.get(0);
    LocalDate index0toVersioningValidTo = editedValidFrom.minusDays(1);
    VersionedObject versionedObjectIndex0 = buildVersionedObjectToUpdate(
        index0toVersioning.getVersionable().getValidFrom(), index0toVersioningValidTo,
        index0toVersioning.getEntity());
    versionedObjects.add(versionedObjectIndex0);
    // 2. Create new version:
    //    versions.get(0)
    //    validFrom = editedValidFrom
    //    validTo = versions.get(0).getValidTo()
    //    update properties with edited properties
    //    VersioningAction = NEW
    Entity entityToAddAfterIndex0 = replaceEditedPropertiesWithCurrentProperties(editedEntity,
        index0toVersioning.getEntity());
    VersionedObject versionedObjectAfterIndex0 = buildVersionedObjectToCreate(editedValidFrom,
        index0toVersioning.getVersionable().getValidTo(), entityToAddAfterIndex0);
    versionedObjects.add(versionedObjectAfterIndex0);

    // 3. versions.get(versions.size()-1)
    //    validFrom = editedValidTo.plusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE
    ToVersioning lastIndexToVersioning = objectToVersioningInValidFromValidToRange.get(
        objectToVersioningInValidFromValidToRange.size() - 1);
    LocalDate lastIndexToVersioningValidFrom = editedValidTo.plusDays(1);
    VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
        lastIndexToVersioningValidFrom, lastIndexToVersioning.getVersionable().getValidTo(),
        lastIndexToVersioning.getEntity());
    versionedObjects.add(versionedObjectLastIndex);
    // 4. Create new version:
    //    versions.get(versions.size()-1)
    //    validFrom =  versions.get(versions.size()-1).getValidFrom()
    //    validTo = editedValidTo
    //    update properties with edited properties
    //    VersioningAction = NEW
    Entity entityToAddBeforeLastIndex = replaceEditedPropertiesWithCurrentProperties(editedEntity,
        index0toVersioning.getEntity());
    VersionedObject versionedObjectBeforeLastIndex = buildVersionedObjectToCreate(
        lastIndexToVersioning.getVersionable()
                             .getValidFrom(),
        editedValidTo, entityToAddBeforeLastIndex);
    versionedObjects.add(versionedObjectBeforeLastIndex);

    // 3. update properties for each version between index = 1 and index = version.size()-1
    //    forEach Versions
    //      update properties
    //      VersioningAction = UPDATE
    for (int i = 1; i < objectToVersioningInValidFromValidToRange.size() - 1; i++) {
      ToVersioning toVersioning = objectToVersioningInValidFromValidToRange.get(i);
      Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
          toVersioning.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToUpdate(
          toVersioning.getVersionable().getValidFrom(),
          toVersioning.getVersionable().getValidTo(),
          entity);
      versionedObjects.add(versionedObject);
    }
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
      Versionable currentVersion, Entity editedEntity, LocalDate editedValidFrom,
      LocalDate editedValidTo,
      List<ToVersioning> objectToVersioningInValidFromValidToRange) {
    //applyVersioningToAnObjectInTheMiddleOfAnExistingEntity
    List<VersionedObject> versionedObjects = new ArrayList<>();
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
    } else{
      throw new IllegalStateException("Something went wrong! If we found just one entity to versioning "
          + " the new version range must be included in the current range!");
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
