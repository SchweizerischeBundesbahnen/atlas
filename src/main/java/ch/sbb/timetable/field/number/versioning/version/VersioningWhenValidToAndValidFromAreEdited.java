package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.date.DateHelper.areDatesSequential;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.areValidToAndPropertiesEdited;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.findObjectToVersioningInValidFromValidToRange;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidToAfterTheRightBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedVersionExactMatchingMultipleVersions;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnlyValidToEditedWithNoEditedProperties;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isVersionOnTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isVersionOnTheRightBorder;
import static java.util.Comparator.comparing;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenValidToAndValidFromAreEdited extends Versioning {

  @Override
  public List<VersionedObject> applyVersioning(Versionable editedVersion,
      Versionable currentVersion,
      List<ToVersioning> objectsToVersioning,
      Entity editedEntity) {

    LocalDate editedValidFrom = editedVersion.getValidFrom();
    if (editedValidFrom == null) {
      log.info("ValidFrom not edited.");
      editedValidFrom = currentVersion.getValidFrom();
    }
    LocalDate editedValidTo = editedVersion.getValidTo();
    if (editedValidTo == null) {
      log.info("ValidTo not edited.");
      editedValidTo = currentVersion.getValidTo();
    }

    //sort objectsToVersioning
    objectsToVersioning.sort(
        comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));

    List<ToVersioning> objectToVersioningFound = findObjectToVersioningInValidFromValidToRange(
        objectsToVersioning, editedValidFrom, editedValidTo);

    List<VersionedObject> versionedObjects = new ArrayList<>(
        fillNotTouchedVersionedObject(objectsToVersioning, objectToVersioningFound));

    if (objectToVersioningFound.isEmpty()) {
      List<VersionedObject> versionedObjectsOnNoObjectFound = applyVersioningWennNoEntityFound(
          editedEntity, editedValidFrom, editedValidTo, objectsToVersioning);
      versionedObjects.addAll(versionedObjectsOnNoObjectFound);
    } else if (objectToVersioningFound.size() == 1) {
      List<VersionedObject> versionedObjectsOnOnlyOneObjectFound = applyVersioningOnSingleFoundEntity(
          editedVersion, currentVersion, editedEntity, editedValidFrom, editedValidTo,
          objectToVersioningFound.get(0));
      versionedObjects.addAll(versionedObjectsOnOnlyOneObjectFound);
    } else {
      List<VersionedObject> versionedObjectsOverMultipleEntity = applyVersioningOverMultipleFoundEntities(
          editedEntity, editedValidFrom, editedValidTo, objectToVersioningFound);
      versionedObjects.addAll(versionedObjectsOverMultipleEntity);
    }

    return versionedObjects;
  }

  //TODO: create class VersioningOnSingleEntity
  private List<VersionedObject> applyVersioningOnSingleFoundEntity(Versionable editedVersion,
      Versionable currentVersion, Entity editedEntity,
      LocalDate editedValidFrom, LocalDate editedValidTo,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    if (isEditedVersionInTheMiddleOfCurrentVersion(editedValidFrom, editedValidTo, toVersioning)) {
      List<VersionedObject> versionedObjectsInTheMiddleOfAnExistingEntity = applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
          currentVersion, editedEntity, editedValidFrom, editedValidTo,
          toVersioning);
      versionedObjects.addAll(versionedObjectsInTheMiddleOfAnExistingEntity);
    } else {
      //check that is really on the border. If There are gap between versions?
      List<VersionedObject> versionedObjectsOnTheBorder = applyVersioningOnTheBorder(editedVersion,
          editedEntity, editedValidFrom, editedValidTo, toVersioning);
      versionedObjects.addAll(versionedObjectsOnTheBorder);
    }

    return versionedObjects;
  }

  //TODO: create class VersioningOnTheBorder
  private List<VersionedObject> applyVersioningOnTheBorder(Versionable editedVersion,
      Entity editedEntity,
      LocalDate editedValidFrom,
      LocalDate editedValidTo, ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isOnlyValidToEditedWithNoEditedProperties(editedVersion, editedEntity)) {
      //Just make the version bigger
      // 1. version
      //    validTo = editedValidTo
      //    do not update properties
      //    VersioningAction = UPDATE
      Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
          toVersioning.getEntity());
      VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
          toVersioning.getVersionable().getValidFrom(), editedValidTo,
          entity);
      versionedObjects.add(versionedObjectLastIndex);

    } else if (areValidToAndPropertiesEdited(editedVersion, editedEntity)) {
      //scenario 8d
      //editedValidTo isAfter toVersioning.validTo
      if (isEditedValidToAfterTheRightBorder(editedValidTo, toVersioning)) {
        Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
            toVersioning.getEntity());
        VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(), editedValidTo,
            entity);
        versionedObjects.add(versionedObjectLastIndex);
      } else {
        log.info("Matched version to split on the right border (validTo and properties edited).");
        //1. currentVersion update
        //    validTo = editedValidTo
        //    merge props
        Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
            toVersioning.getEntity());
        VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(), editedValidTo,
            entity);
        versionedObjects.add(versionedObjectLastIndex);

        //2. new version
        //    validFrom = editedValidTo.plusDay(1)
        //    validTo = currentVersion.validTo()
        //    copy currentVersion Props
        VersionedObject versionedObjectCreated = buildVersionedObjectToCreate(
            editedValidTo.plusDays(1),
            toVersioning.getVersionable()
                        .getValidTo(), toVersioning.getEntity());
        versionedObjects.add(versionedObjectCreated);
      }
    } else {
      // 1. version
      //    validTo = editedValidFrom.minusDay(1)
      //    do not update properties
      //    VersioningAction = UPDATE
      VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
          toVersioning.getVersionable().getValidFrom(), editedValidFrom.minusDays(1),
          toVersioning.getEntity());
      versionedObjects.add(versionedObjectLastIndex);

      // 2. Create new version:
      //    versions.get(0)
      //    validFrom = editedValidFrom
      //    validTo = versions.get(0).getValidTo()
      //    update properties with edited properties
      //    VersioningAction = NEW
      Entity entityToAddAfterLastIndex = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          toVersioning.getEntity());
      VersionedObject versionedObjectAfterIndex0 = buildVersionedObjectToCreate(editedValidFrom,
          editedValidTo, entityToAddAfterLastIndex);
      versionedObjects.add(versionedObjectAfterIndex0);
    }
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningWennNoEntityFound(Entity editedEntity,
      LocalDate editedValidFrom,
      LocalDate editedValidTo,
      List<ToVersioning> objectsToVersioning) {

    List<VersionedObject> versionedObjects = new ArrayList<>();

    // On the right border
    ToVersioning rightBorderVersion = objectsToVersioning.get(objectsToVersioning.size() - 1);
    if (isVersionOnTheRightBorder(rightBorderVersion, editedValidFrom)) {
      Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          rightBorderVersion.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToCreate(editedValidFrom,
          editedValidTo, entityToAdd);
      versionedObjects.add(versionedObject);
    }

    // On the left border
    ToVersioning leftBorderVersion = objectsToVersioning.get(0);
    if (isVersionOnTheLeftBorder(leftBorderVersion, editedValidTo)) {
      Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          leftBorderVersion.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToCreate(editedValidFrom,
          editedValidTo, entityToAdd);
      versionedObjects.add(versionedObject);
    }
    return versionedObjects;
  }

  //TODO: create class VersioningOverMultipleEntities
  private List<VersionedObject> applyVersioningOverMultipleFoundEntities(Entity editedEntity,
      LocalDate editedValidFrom, LocalDate editedValidTo,
      List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    //1. if (toVersioningList(0).getValidFrom() == editedValidFrom &&
    //      toVersioningList(toVersioningList.size()-1).getValidTo() == editedValidTo)
    //  then
    //    forEach version update only the properties
    if (isEditedVersionExactMatchingMultipleVersions(editedValidFrom, editedValidTo,
        toVersioningList)) {
      //scenario 1d
      log.info(
          "Matched multiple versions on the borders: editedValidFrom is equal to the first matched version validFrom"
              + " and the editedValidTo is equal to the last matched version ValidTo.");
      for (ToVersioning toVersioning : toVersioningList) {
        Entity entityToUpdate = replaceEditedPropertiesWithCurrentProperties(editedEntity,
            toVersioning.getEntity());
        VersionedObject versionedObjectAfterIndex0 = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(),
            toVersioning.getVersionable().getValidTo(), entityToUpdate);
        versionedObjects.add(versionedObjectAfterIndex0);
      }
    } else if (VersioningHelper.isThereGapBetweenVersions(toVersioningList)) {
      applyVersioningWhenThereIsGapBetweenVersionsFound(editedEntity, editedValidTo,
          toVersioningList, versionedObjects);
    } else {
      //scenario5, scenario6,scenario3
      log.info("Matched multiple versions over the borders.");
      //applyVersioningOverMultipleObjects
      //Found more than one versions
      // 1. versions.get(0)
      //    validTo = editedValidFrom.minusDay(1)
      //    do not update properties
      //    VersioningAction = UPDATE
      ToVersioning index0toVersioning = toVersioningList.get(0);
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
      ToVersioning lastIndexToVersioning = toVersioningList.get(
          toVersioningList.size() - 1);
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
          lastIndexToVersioning.getEntity());
      VersionedObject versionedObjectBeforeLastIndex = buildVersionedObjectToCreate(
          lastIndexToVersioning.getVersionable()
                               .getValidFrom(),
          editedValidTo, entityToAddBeforeLastIndex);
      versionedObjects.add(versionedObjectBeforeLastIndex);

      // 3. update properties for each version between index = 1 and index = version.size()-1
      //    forEach Versions
      //      update properties
      //      VersioningAction = UPDATE
      for (int i = 1; i < toVersioningList.size() - 1; i++) {
        ToVersioning toVersioning = toVersioningList.get(i);
        Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
            toVersioning.getEntity());
        VersionedObject versionedObject = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(),
            toVersioning.getVersionable().getValidTo(),
            entity);
        versionedObjects.add(versionedObject);
      }
    }
    return versionedObjects;
  }

  private void applyVersioningWhenThereIsGapBetweenVersionsFound(Entity editedEntity,
      LocalDate editedValidTo,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    log.info("Matched multiple versions with gap");
    for (int i = 0; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i);
      //if it has next version
      if ((i + 1) < toVersioningList.size()) {
        ToVersioning next = toVersioningList.get(i + 1);
        if (!areDatesSequential(current.getVersionable().getValidTo(),
            next.getVersionable().getValidFrom())) {
          log.info("Matched gap {} - {}", current.getVersionable().getValidTo(),
              next.getVersionable().getValidFrom());
          log.info("{}\n{}", current, next);
          //1. current UPDATE
          //    current.validTo=editedValidFrom-1
          //    merge props with edited props
          Entity currentEntityToUpdate = replaceEditedPropertiesWithCurrentProperties(
              editedEntity,
              current.getEntity());
          VersionedObject versionedObjectFillGap = buildVersionedObjectToUpdate(
              current.getVersionable().getValidFrom(),
              next.getVersionable().getValidFrom().minusDays(1), currentEntityToUpdate);
          versionedObjects.add(versionedObjectFillGap);
        } else {
          applyVersioningWhenThereIsGapNearToTheVersion(editedEntity, editedValidTo,
              versionedObjects, current);
        }

      } else {
        applyVersioningWhenThereIsGapNearToTheVersion(editedEntity, editedValidTo, versionedObjects,
            current);
      }
    }
  }

  private void applyVersioningWhenThereIsGapNearToTheVersion(Entity editedEntity,
      LocalDate editedValidTo,
      List<VersionedObject> versionedObjects, ToVersioning current) {
    if (editedValidTo.isAfter(current.getVersionable().getValidTo())) {
      //just update current version properties
      //just fill the gap
      Entity entityToUpdate = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          current.getEntity());
      VersionedObject updateCurrentVersionedObject = buildVersionedObjectToUpdate(
          current.getVersionable().getValidFrom(),
          current.getVersionable().getValidTo(), entityToUpdate);
      versionedObjects.add(updateCurrentVersionedObject);
    }
    if (editedValidTo.isBefore(current.getVersionable().getValidTo())) {
      //split versions
      //2. NEW version
      //    validFrom=next.getValidFrom
      //    validTo=editedValidTo
      //    merge props next + edited
      Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          current.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToCreate(
          current.getVersionable().getValidFrom(),
          editedValidTo, entityToAdd);
      versionedObjects.add(versionedObject);

      ///3. next version UPDATE
      //   validFrom = editedValidTo +1
      VersionedObject nextVersionedObject = buildVersionedObjectToUpdate(
          editedValidTo.plusDays(1),
          current.getVersionable().getValidTo(), current.getEntity());
      versionedObjects.add(nextVersionedObject);
    }
  }

  private List<VersionedObject> applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
      Versionable currentVersion, Entity editedEntity, LocalDate editedValidFrom,
      LocalDate editedValidTo,
      ToVersioning toVersioning) {
    //applyVersioningToAnObjectInTheMiddleOfAnExistingEntity
    List<VersionedObject> versionedObjects = new ArrayList<>();
    // Scenario2:
    // The edited version is in the middle of an existing Version
    if (isEditedVersionInTheMiddleOfCurrentVersion(editedValidFrom, editedValidTo, toVersioning)) {
      log.info("Found in the middle of an exiting Version -> Scenario 2");

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
    } else {
      throw new IllegalStateException(
          "Something went wrong! If we found just one entity to versioning "
              + " the new version range must be included in the current range!");
    }
    return versionedObjects;
  }

}
