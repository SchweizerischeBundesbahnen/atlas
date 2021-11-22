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
      List<VersionedObject> versionedObjectsOnTheBorder =
          applyVersioningOnTheBorderOfSingleFoundEntity(vd, toVersioning);
      versionedObjects.addAll(versionedObjectsOnTheBorder);
      return versionedObjects;
    }
  }

  private List<VersionedObject> applyVersioningInTheMiddleOfAnExistingEntity(VersioningData vd,
      ToVersioning toVersioning) {
    log.info("Found in the middle of an exiting.");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    VersionedObject currentVersionUpdate = shortenLeftCurrentVersion(vd, toVersioning);
    versionedObjects.add(currentVersionUpdate);
    VersionedObject toCreateVersionedObject = shortenOnRightCurrentVersion(vd, toVersioning);
    versionedObjects.add(toCreateVersionedObject);
    VersionedObject toAddAtEndVersionedObject = createNewVersionInTheMiddle(vd, toVersioning);
    versionedObjects.add(toAddAtEndVersionedObject);
    return versionedObjects;
  }

  private VersionedObject shortenLeftCurrentVersion(VersioningData vd, ToVersioning toVersioning) {
    LocalDate toUpdateValidTo = vd.getEditedValidFrom().minusDays(1);
    return buildVersionedObjectToUpdate(toVersioning.getVersionable().getValidFrom(),
        toUpdateValidTo,
        toVersioning.getEntity());
  }

  private VersionedObject shortenOnRightCurrentVersion(VersioningData vd,
      ToVersioning toVersioning) {
    Entity entityToAddAtEnd = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());
    return buildVersionedObjectToCreate(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        entityToAddAtEnd);
  }

  private VersionedObject createNewVersionInTheMiddle(VersioningData vd,
      ToVersioning toVersioning) {
    LocalDate toAddAtEndValidFrom = vd.getEditedValidTo().plusDays(1);
    LocalDate toAddAtEndValidTo = vd.getCurrentVersion().getValidTo();
    return buildVersionedObjectToCreate(toAddAtEndValidFrom, toAddAtEndValidTo,
        toVersioning.getEntity());
  }

  private List<VersionedObject> applyVersioningOnTheBorderOfSingleFoundEntity(VersioningData vd,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    if (isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioning)) {
      log.info("Found on the left border, "
          + "editedValidFrom is before current validFrom and validTo is not edited.");
      // update validFrom=editedValidFrom and merge properties
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          vd, toVersioning);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }
    if (isOnTheRightBorderAndOnlyValidToIsEditedWithNoEditedProperties(vd.getEditedVersion(),
        vd.getEditedEntity())) {
      log.info("Found on the right border, validTo is edited, no properties are edited.");
      // update validTo=editedValidTo and merge properties
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd,
          toVersioning);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }
    if (isOnTheRightBorderAndValidToAndPropertiesAreEdited(vd.getEditedVersion(),
        vd.getEditedEntity())) {
      return applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(vd, toVersioning);
    }
    if (isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioning)) {
      return applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(vd, toVersioning);
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(
      VersioningData vd,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isEditedValidToAfterTheRightBorder(vd.getEditedValidTo(), toVersioning)) {
      log.info(
          "Found on the right border, validTo is after current validTo, properties are edited.");
      // update validTo=editedValidTo and update properties
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd,
          toVersioning);
      versionedObjects.add(versionedObject);
    } else {
      log.info("Found version to split on the right border, validTo and properties edited.");
      // update validTo=editedValidTo and update properties
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd,
          toVersioning);
      versionedObjects.add(versionedObject);
      // add new version after the current edited version
      VersionedObject versionedObjectCreated = addNewVersionOnTheCurrentRightBorder(vd,
          toVersioning);
      versionedObjects.add(versionedObjectCreated);
    }
    return versionedObjects;
  }

  private VersionedObject addNewVersionOnTheCurrentRightBorder(VersioningData vd,
      ToVersioning toVersioning) {
    return buildVersionedObjectToCreate(
        vd.getEditedValidTo().plusDays(1),
        toVersioning.getVersionable().getValidTo(), toVersioning.getEntity());
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(
      VersioningData vd, ToVersioning toVersioning) {
    log.info("Found version to split on or over the right border, validTo and properties edited.");

    List<VersionedObject> versionedObjects = new ArrayList<>();
    // 1. version
    //    validTo = editedValidFrom.minusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE
    VersionedObject shortenRightVersion = shortenRightVersion(vd, toVersioning);
    versionedObjects.add(shortenRightVersion);

    // 2. Create new version:
    //    versions.get(0)
    //    validFrom = editedValidFrom
    //    validTo = versions.get(0).getValidTo()
    //    update properties with edited properties
    //    VersioningAction = NEW
    VersionedObject newVersionAfterTheRightBorder = addNewVersionAfterTheRightBorder(vd,
        toVersioning);
    versionedObjects.add(newVersionAfterTheRightBorder);
    return versionedObjects;
  }

  private VersionedObject addNewVersionAfterTheRightBorder(VersioningData vd,
      ToVersioning toVersioning) {
    Entity entityToAddAfterLastIndex = replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(),
        toVersioning.getEntity());
    return buildVersionedObjectToCreate(
        vd.getEditedValidFrom(),
        vd.getEditedValidTo(), entityToAddAfterLastIndex);
  }

  private VersionedObject shortenRightVersion(VersioningData vd, ToVersioning toVersioning) {
    return buildVersionedObjectToUpdate(
        toVersioning.getVersionable().getValidFrom(), vd.getEditedValidFrom().minusDays(1),
        toVersioning.getEntity());
  }

  private VersionedObject shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(VersioningData vd,
      ToVersioning toVersioning) {
    Entity entity = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());
    return buildVersionedObjectToUpdate(vd.getEditedValidFrom(), vd.getEditedValidTo(), entity);
  }

}
