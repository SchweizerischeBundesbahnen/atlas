package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.areValidToAndPropertiesEdited;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidToAfterTheRightBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnlyValidToEditedWithNoEditedProperties;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningOnSingleFoundEntity implements Versioning{

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning wenn just one entity to versioning found.");
    ToVersioning toVersioning = vd.getSingleFoundObjectToVersioning();
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isEditedVersionInTheMiddleOfCurrentVersion(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioning)) {
      List<VersionedObject> versionedObjectsInTheMiddleOfAnExistingEntity = applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
          vd.getEditedValidFrom(), vd.getEditedValidTo(), vd.getCurrentVersion(),
          vd.getEditedEntity(),
          toVersioning);
      versionedObjects.addAll(versionedObjectsInTheMiddleOfAnExistingEntity);
      return versionedObjects;
    } else {
      List<VersionedObject> versionedObjectsOnTheBorder = applyVersioningOnTheBorderOnSingleFoundEntity(
          vd.getEditedValidFrom(), vd.getEditedValidTo(), vd.getEditedVersion(),
          vd.getEditedEntity(), toVersioning);
      versionedObjects.addAll(versionedObjectsOnTheBorder);
      return versionedObjects;
    }
  }

  private List<VersionedObject> applyVersioningToAnObjectInTheMiddleOfAnExistingEntity(
      LocalDate editedValidFrom, LocalDate editedValidTo, Versionable currentVersion,
      Entity editedEntity,
      ToVersioning toVersioning) {
    log.info("Apply versioning wenn just one entity match the middle of an existing Entity.");
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
      return versionedObjects;
    }
    throw new VersioningException(
        "Something went wrong! If we found just one entity to versioning "
            + " the new version range must be included in the current range!");
  }

  //TODO: create class VersioningOnTheBorder
  private List<VersionedObject> applyVersioningOnTheBorderOnSingleFoundEntity(
      LocalDate editedValidFrom, LocalDate editedValidTo, Versionable editedVersion,
      Entity editedEntity,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    //left border no matter if properties and or validFrom is changed
    if (isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(editedValidFrom, editedValidTo,
        toVersioning)) {
      //1. update validFrom with editedValidTo
      //2. merge properties
      Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
          toVersioning.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToUpdate(
          editedValidFrom, toVersioning.getVersionable().getValidTo(),
          entity);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }

    //right border without properties changes
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
      return versionedObjects;

    }
    //right border with properties changes
    if (areValidToAndPropertiesEdited(editedVersion, editedEntity)) {
      //scenario 8d
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
      return versionedObjects;
    }
    //right border and validTo is bigger/equals than current validTo
    //Scenario6 when only validFrom is edited with properties changes, Szenario 6: Neue Version in der Zukunft, die letzte Version überschneidet
    if (isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder(editedValidFrom, editedValidTo,
        toVersioning)) {
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
      return versionedObjects;
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");

  }


}
