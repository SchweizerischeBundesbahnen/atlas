package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidToAfterTheRightBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheRightBorderAndOnlyValidToIsEditedWithNoEditedProperties;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnTheRightBorderAndValidToAndPropertiesAreEdited;

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
public class VersioningOnSingleFoundEntity implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning wenn just one entity to versioning found.");
    ToVersioning toVersioning = vd.getSingleFoundObjectToVersioning();
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isEditedVersionInTheMiddleOfCurrentEntity(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioning)) {
      List<VersionedObject> versionedObjectsInTheMiddleOfAnExistingEntity =
          applyVersioningInTheMiddleOfAnExistingEntity(vd, toVersioning);
      versionedObjects.addAll(versionedObjectsInTheMiddleOfAnExistingEntity);
      return versionedObjects;
    } else {
      List<VersionedObject> versionedObjectsOnTheBorder = applyVersioningOnTheBorderOfSingleFoundEntity(
          vd.getEditedValidFrom(), vd.getEditedValidTo(), vd.getEditedVersion(),
          vd.getEditedEntity(), toVersioning);
      versionedObjects.addAll(versionedObjectsOnTheBorder);
      return versionedObjects;
    }
  }

  private List<VersionedObject> applyVersioningInTheMiddleOfAnExistingEntity(VersioningData vd,
      ToVersioning toVersioning) {
    log.info("Found in the middle of an exiting.");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    return applyVersioningInTheMiddleOfCurrentEntity(vd, toVersioning, versionedObjects);
  }

  private List<VersionedObject> applyVersioningInTheMiddleOfCurrentEntity(VersioningData vd,
      ToVersioning toVersioning,
      List<VersionedObject> versionedObjects) {
    //1. Update the existing version:
    // currentVersion.setValidTo(editedValidFrom.minusDay(1))
    // do not change properties
    LocalDate toUpdateValidTo = vd.getEditedValidFrom().minusDays(1);
    VersionedObject toUpdateVersionedObject = buildVersionedObjectToUpdate(
        toVersioning.getVersionable().getValidFrom(), toUpdateValidTo,
        toVersioning.getEntity());
    versionedObjects.add(toUpdateVersionedObject);
    //2. Copy the currentVersion splittedCurrentVersion.copy(currentVersion)
    //  splittedCurrentVersion.setValidFrom(editedValidTo.plusDay(1))
    //  splittedCurrentVersion.setValidTo(currentVersion.ValidTo())
    // do not change properties
    LocalDate toAddAtEndValidFrom = vd.getEditedValidTo().plusDays(1);
    LocalDate toAddAtEndValidTo = vd.getCurrentVersion().getValidTo();
    VersionedObject toAddAtEndVersionedObject = buildVersionedObjectToCreate(
        toAddAtEndValidFrom,
        toAddAtEndValidTo, toVersioning.getEntity());
    versionedObjects.add(toAddAtEndVersionedObject);
    //3. Create a new Version
    // newVersion.setValidFrom(edited.validFrom())
    // newVersion.setValidTo(edited.validTo())
    // copy all properties from currentVersion and replace them with the edited properties
    Entity entityToAddAtEnd = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());
    VersionedObject toCreateVersionedObject = buildVersionedObjectToCreate(
        vd.getEditedValidFrom(),
        vd.getEditedValidTo(), entityToAddAtEnd);
    versionedObjects.add(toCreateVersionedObject);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningOnTheBorderOfSingleFoundEntity(
      LocalDate editedValidFrom, LocalDate editedValidTo, Versionable editedVersion,
      Entity editedEntity,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    if (isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(editedValidFrom, editedValidTo,
        toVersioning)) {
      log.info(
          "Found on the left border, editedValidTo is before current validFrom and validTo is not edited.");
      //1. update validFrom with editedValidTo
      //2. merge properties
      applyVersioningUpdateOnTheBorder(editedValidFrom, toVersioning.getVersionable().getValidTo(),
          editedEntity, toVersioning, versionedObjects);
      return versionedObjects;
    }
    if (isOnTheRightBorderAndOnlyValidToIsEditedWithNoEditedProperties(editedVersion,
        editedEntity)) {
      log.info("Found on the right border, validTo is edited, no properties are edited.");
      //Just make the version bigger
      // 1. validTo = editedValidTo
      //    do not update properties
      //    VersioningAction = UPDATE
      applyVersioningUpdateOnTheBorder(toVersioning.getVersionable().getValidFrom(), editedValidTo,
          editedEntity,
          toVersioning, versionedObjects);
      return versionedObjects;
    }
    if (isOnTheRightBorderAndValidToAndPropertiesAreEdited(editedVersion, editedEntity)) {
      return applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(editedValidTo,
          editedEntity, toVersioning, versionedObjects);
    }
    if (isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder(editedValidFrom, editedValidTo,
        toVersioning)) {
      return applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(editedValidFrom,
          editedValidTo, editedEntity, toVersioning,
          versionedObjects);
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(
      LocalDate editedValidTo, Entity editedEntity,
      ToVersioning toVersioning, List<VersionedObject> versionedObjects) {
    if (isEditedValidToAfterTheRightBorder(editedValidTo, toVersioning)) {
      log.info(
          "Found on the right border, validTo is after current validTo, properties are edited.");
      applyVersioningUpdateOnTheBorder(toVersioning.getVersionable().getValidFrom(),
          editedValidTo, editedEntity,
          toVersioning, versionedObjects);
    } else {
      log.info("Found version to split on the right border, validTo and properties edited.");
      //1. currentVersion update
      //    validTo = editedValidTo
      //    merge props
      applyVersioningUpdateOnTheBorder(toVersioning.getVersionable().getValidFrom(),
          editedValidTo, editedEntity,
          toVersioning, versionedObjects);
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

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(
      LocalDate editedValidFrom,
      LocalDate editedValidTo, Entity editedEntity, ToVersioning toVersioning,
      List<VersionedObject> versionedObjects) {
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

  private void applyVersioningUpdateOnTheBorder(LocalDate editedValidFrom, LocalDate validTo,
      Entity editedEntity,
      ToVersioning toVersioning,
      List<VersionedObject> versionedObjects) {
    Entity entity = replaceEditedPropertiesWithCurrentProperties(editedEntity,
        toVersioning.getEntity());
    VersionedObject versionedObject = buildVersionedObjectToUpdate(
        editedValidFrom, validTo,
        entity);
    versionedObjects.add(versionedObject);
  }

}
