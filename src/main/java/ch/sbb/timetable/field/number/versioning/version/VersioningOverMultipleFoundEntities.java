package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.date.DateHelper.areDatesSequential;
import static ch.sbb.timetable.field.number.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.areBothValidToAndValidFromChanged;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.hasNextVersion;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isBetweenMultipleVersionsAndOverTheBorders;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isCurrentVersionBetweenEditedValidFromAndEditedValidTo;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidFromExactOnTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidFromOverTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidToExactOnTheRightBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedValidToOverTheRightBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isEditedVersionExactMatchingMultipleVersions;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isOnlyValidToChanged;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isThereGapBetweenVersions;

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
public class VersioningOverMultipleFoundEntities implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    List<ToVersioning> toVersioningList = vd.getObjectToVersioningFound();

    if (isEditedVersionExactMatchingMultipleVersions(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningExactMatchingMultipleVersions(vd, toVersioningList);
    }
    if (isThereGapBetweenVersions(toVersioningList)) {
      return applyVersioningWhenThereIsGapBetweenVersionsFound(vd, toVersioningList);
    }
    if (isBetweenMultipleVersionsAndOverTheBorders(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningBetweenMultipleEntitiesOverTheBorders(vd, toVersioningList);
    }
    if (isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)
        || isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)) {
      return applyVersioningOverTheBorders(vd, toVersioningList);
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private List<VersionedObject> applyVersioningExactMatchingMultipleVersions(VersioningData vd,
      List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    log.info(
        "Matched multiple versions on the borders: editedValidFrom is equal to the first matched version validFrom"
            + " and the editedValidTo is equal to the last matched version ValidTo.");
    for (ToVersioning toVersioning : toVersioningList) {
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          toVersioning.getValidFrom(), toVersioning.getValidTo(),
          toVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedObject);
    }
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningOverTheBorders(VersioningData vd,
      List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    applyVersioningToTheLeftBorder(vd, versionedObjects, toVersioningList);
    applyVersioningToTheRightBorder(vd, versionedObjects, toVersioningList);
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private void applyVersioningToTheLeftBorder(VersioningData vd,
      List<VersionedObject> versionedObjects,
      List<ToVersioning> toVersioningList) {
    ToVersioning leftBorderToVersioning = toVersioningList.get(0);
    if (isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)) {
      //update validTo and merge props
      VersionedObject versionedLeftBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          vd.getEditedValidFrom(),
          leftBorderToVersioning.getValidTo(), leftBorderToVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedLeftBorder);
    } else if (isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
        leftBorderToVersioning)) {
      //just update props
      VersionedObject versionedLeftBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          leftBorderToVersioning.getValidFrom(),
          leftBorderToVersioning.getValidTo(), leftBorderToVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedLeftBorder);

    } else if (!isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)
        && !isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
        leftBorderToVersioning)) {
      applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd, toVersioningList,
          versionedObjects);
    } else {
      throw new VersioningException(
          "Something went wrong. I'm not able to apply versioning on this scenario.");
    }
  }


  private void applyVersioningToTheRightBorder(VersioningData vd,
      List<VersionedObject> versionedObjects,
      List<ToVersioning> toVersioningList) {
    ToVersioning rightBorderToVersioning = toVersioningList.get(
        toVersioningList.size() - 1);
    if (isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)) {
      //update validTo and merge props
      VersionedObject versionedRightBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          rightBorderToVersioning.getValidFrom(), vd.getEditedValidTo(),
          rightBorderToVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedRightBorder);
    } else if (isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(),
        rightBorderToVersioning)) {
      //just update props
      VersionedObject versionedRightBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          rightBorderToVersioning.getValidFrom(),
          rightBorderToVersioning.getValidTo(),
          rightBorderToVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedRightBorder);

    } else if (!isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)
        && !isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(), rightBorderToVersioning)) {
      applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd, toVersioningList,
          versionedObjects);
    } else {
      throw new VersioningException(
          "Something went wrong. I'm not able to apply versioning on this scenario.");
    }
  }

  private List<VersionedObject> applyVersioningBetweenMultipleEntitiesOverTheBorders(
      VersioningData vd, List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    //scenario5, scenario6,scenario3
    log.info("Matched multiple versions over the borders.");
    applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd, toVersioningList,
        versionedObjects);
    applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd, toVersioningList,
        versionedObjects);
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningWhenThereIsGapBetweenVersionsFound(VersioningData vd,
      List<ToVersioning> toVersioningList) {
    log.info("Matched multiple versions with gap");

    if (isOnlyValidToChanged(vd.getEditedVersion(), vd.getCurrentVersion())
        || areBothValidToAndValidFromChanged(vd.getEditedVersion(), vd.getCurrentVersion())) {
      List<VersionedObject> versionedObjects = new ArrayList<>();
      for (int i = 0; i < toVersioningList.size(); i++) {
        ToVersioning current = toVersioningList.get(i);

        if (hasNextVersion(toVersioningList, i)) {
          ToVersioning next = toVersioningList.get(i + 1);

          if (!areDatesSequential(current.getValidTo(),
              next.getValidFrom())) {
            log.info("Matched gap {} - {}", current.getValidTo(),
                next.getValidFrom());
            log.info("{}\n{}", current, next);

            if (isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(
                vd.getEditedValidFrom(),
                current)) {
              //1. current Version UPDATE
              //   current.validTo=editedValidTo-1
              VersionedObject versionedObjectToUpdate = buildVersionedObjectToUpdate(
                  current.getValidFrom(),
                  vd.getEditedValidFrom().minusDays(1), current.getEntity());
              versionedObjects.add(versionedObjectToUpdate);
              //2. create new Version
              //  new.validFrom=editedValidFom
              //  new.validTo=next.validFrom-1
              //  merge properties
              Entity currentEntityToCreate = replaceEditedPropertiesWithCurrentProperties(
                  vd.getEditedEntity(),
                  current.getEntity());
              VersionedObject versionedObjectFillGap = buildVersionedObjectToCreate(
                  vd.getEditedValidFrom(),
                  next.getValidFrom().minusDays(1), currentEntityToCreate);
              versionedObjects.add(versionedObjectFillGap);
            }
            //1.case: perfect match editedValidFrom == current.validFrom
            //2.case: we are in the middle of multiple versions with gap
            // and we are matching the second item (e.g.) where:
            // editValidFrom < 2.validFrom && editedValidTo > 2.validTo
            // edited  |-------------------------------------------------|
            // current |--------------|    |---------------|    |---------------|
            //                1                     2                   3
            else if (isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(), current) ||
                isCurrentVersionBetweenEditedValidFromAndEditedValidTo(vd.getEditedValidFrom(),
                    vd.getEditedValidTo(), current)) {
              //1. current UPDATE
              //    current.validTo=editedValidFrom-1
              //    merge props with edited props
              VersionedObject versionedObjectFillGap = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
                  current.getValidFrom(),
                  next.getValidFrom().minusDays(1), current,
                  vd.getEditedEntity()
              );
              versionedObjects.add(versionedObjectFillGap);
            } else {
              throw new VersioningException(
                  "Something went wrong. I'm not able to apply versioning on this scenario.");
            }

          } else if (areDatesSequential(current.getValidTo(),
              next.getValidFrom())) {
            //versions current and next are sequential
            applyVersioningWhenThereIsGapNearToTheVersion(vd.getEditedValidTo(),
                vd.getEditedEntity(),
                current, versionedObjects);
          } else {
            throw new VersioningException(
                "Something went wrong. I'm not able to apply versioning on this scenario.");
          }
        } else if (!hasNextVersion(toVersioningList, i)) {
          //does not have next
          applyVersioningWhenThereIsGapNearToTheVersion(vd.getEditedValidTo(), vd.getEditedEntity(),
              current, versionedObjects
          );
        } else {
          throw new VersioningException(
              "Something went wrong. I'm not able to apply versioning on this scenario.");
        }
      }
      return versionedObjects;
    }

    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private void applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    //applyVersioningOverMultipleObjects
    //Found more than one versions
    // 1. versions.get(0)
    //    validTo = editedValidFrom.minusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE

    //if validFrom is before index0.getValidFrom
    // split versions
    ToVersioning index0toVersioning = toVersioningList.get(0);
    LocalDate index0toVersioningValidTo = vd.getEditedValidFrom().minusDays(1);
    VersionedObject versionedObjectIndex0 = buildVersionedObjectToUpdate(
        index0toVersioning.getValidFrom(), index0toVersioningValidTo,
        index0toVersioning.getEntity());
    versionedObjects.add(versionedObjectIndex0);
    // 2. Create new version:
    //    versions.get(0)
    //    validFrom = editedValidFrom
    //    validTo = versions.get(0).getValidTo()
    //    update properties with edited properties
    //    VersioningAction = NEW
    Entity entityToAddAfterIndex0 = replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(),
        index0toVersioning.getEntity());
    VersionedObject versionedObjectAfterIndex0 = buildVersionedObjectToCreate(
        vd.getEditedValidFrom(),
        index0toVersioning.getValidTo(), entityToAddAfterIndex0);
    versionedObjects.add(versionedObjectAfterIndex0);
  }


  private void applyVersioningWhenThereIsGapNearToTheVersion(LocalDate editedValidTo,
      Entity editedEntity,
      ToVersioning current, List<VersionedObject> versionedObjects) {
    if (editedValidTo.isAfter(current.getValidTo())) {
      //just update current version properties
      //just fill the gap
      VersionedObject updateCurrentVersionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          current.getValidFrom(), current.getValidTo(), current,
          editedEntity
      );
      versionedObjects.add(updateCurrentVersionedObject);
    }
    if (editedValidTo.isBefore(current.getValidTo())) {
      //split versions
      //2. NEW version
      //    validFrom=next.getValidFrom
      //    validTo=editedValidTo
      //    merge props next + edited
      Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
          editedEntity,
          current.getEntity());
      VersionedObject versionedObject = buildVersionedObjectToCreate(
          current.getValidFrom(),
          editedValidTo, entityToAdd);
      versionedObjects.add(versionedObject);

      ///3. next version UPDATE
      //   validFrom = editedValidTo +1
      VersionedObject nextVersionedObject = buildVersionedObjectToUpdate(
          editedValidTo.plusDays(1),
          current.getValidTo(), current.getEntity());
      versionedObjects.add(nextVersionedObject);
    }
  }


  private void applyVersioningBetweenLeftAndRightBorder(VersioningData vd,
      List<ToVersioning> toVersioningList,
      List<VersionedObject> versionedObjects) {
    // 3. update properties for each version between index = 1 and index = version.size()-1
    //    forEach Versions
    //      update properties
    //      VersioningAction = UPDATE
    for (int i = 1; i < toVersioningList.size() - 1; i++) {
      ToVersioning toVersioning = toVersioningList.get(i);
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          toVersioning.getValidFrom(), toVersioning.getValidTo(),
          toVersioning, vd.getEditedEntity()
      );
      versionedObjects.add(versionedObject);
    }
  }

  private void applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    // 3. versions.get(versions.size()-1)
    //    validFrom = editedValidTo.plusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE

    //if validTo is after lastIndex0.getValidTo
    // split versions
    ToVersioning lastIndexToVersioning = toVersioningList.get(
        toVersioningList.size() - 1);
    LocalDate lastIndexToVersioningValidFrom = vd.getEditedValidTo().plusDays(1);
    VersionedObject versionedObjectLastIndex = buildVersionedObjectToUpdate(
        lastIndexToVersioningValidFrom, lastIndexToVersioning.getValidTo(),
        lastIndexToVersioning.getEntity());
    versionedObjects.add(versionedObjectLastIndex);
    // 4. Create new version:
    //    versions.get(versions.size()-1)
    //    validFrom =  versions.get(versions.size()-1).getValidFrom()
    //    validTo = editedValidTo
    //    update properties with edited properties
    //    VersioningAction = NEW
    Entity entityToAddBeforeLastIndex = replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(),
        lastIndexToVersioning.getEntity());
    VersionedObject versionedObjectBeforeLastIndex = buildVersionedObjectToCreate(
        lastIndexToVersioning.getValidFrom(),
        vd.getEditedValidTo(), entityToAddBeforeLastIndex);
    versionedObjects.add(versionedObjectBeforeLastIndex);
  }

  private VersionedObject shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
      LocalDate editedValidFrom, LocalDate validTo,
      ToVersioning toVersioning,
      Entity editedEntity) {
    Entity entityLeftBorderToUpdate = replaceEditedPropertiesWithCurrentProperties(
        editedEntity,
        toVersioning.getEntity());
    return buildVersionedObjectToUpdate(
        editedValidFrom, validTo,
        entityLeftBorderToUpdate);
  }

}
