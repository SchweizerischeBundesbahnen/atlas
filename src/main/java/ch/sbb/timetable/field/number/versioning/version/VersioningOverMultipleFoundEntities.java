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
import ch.sbb.timetable.field.number.versioning.model.Versionable;
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
    List<VersionedObject> versionedObjects = new ArrayList<>();
    List<ToVersioning> toVersioningList = vd.getObjectToVersioningFound();
    //1. if (toVersioningList(0).getValidFrom() == editedValidFrom &&
    //      toVersioningList(toVersioningList.size()-1).getValidTo() == editedValidTo)
    //  then
    //    forEach version update only the properties
    if (isEditedVersionExactMatchingMultipleVersions(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioningList)) {
      //scenario 1d
      log.info(
          "Matched multiple versions on the borders: editedValidFrom is equal to the first matched version validFrom"
              + " and the editedValidTo is equal to the last matched version ValidTo.");
      for (ToVersioning toVersioning : toVersioningList) {
        Entity entityToUpdate = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
            toVersioning.getEntity());
        VersionedObject versionedObjectAfterIndex0 = buildVersionedObjectToUpdate(
            toVersioning.getVersionable().getValidFrom(),
            toVersioning.getVersionable().getValidTo(), entityToUpdate);
        versionedObjects.add(versionedObjectAfterIndex0);
      }
      return versionedObjects;
    }
    if (isThereGapBetweenVersions(toVersioningList)) {
      return applyVersioningWhenThereIsGapBetweenVersionsFound(vd.getEditedValidFrom(),
          vd.getEditedValidTo(),
          vd.getEditedVersion(), vd.getEditedVersion(), vd.getEditedEntity(),
          toVersioningList);
    }
    if (isBetweenMultipleVersionsAndOverTheBorders(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioningList)) {
      //scenario5, scenario6,scenario3
      log.info("Matched multiple versions over the borders.");
      applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd.getEditedValidFrom(),
          vd.getEditedEntity(),
          toVersioningList, versionedObjects);

      applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd.getEditedValidTo(),
          vd.getEditedEntity(),
          toVersioningList, versionedObjects);

      applyVersioningBetweenLeftAndRightBorder(vd.getEditedEntity(), toVersioningList,
          versionedObjects);
      return versionedObjects;
    }
    if (isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)
        || isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)) {
      if (isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)) {
        //update validTo and merge props
        ToVersioning leftBorderToVersioning = toVersioningList.get(0);
        Entity entityLeftBorderToUpdate = replaceEditedPropertiesWithCurrentProperties(
            vd.getEditedEntity(),
            leftBorderToVersioning.getEntity());
        VersionedObject versionedLeftBorder = buildVersionedObjectToUpdate(
            vd.getEditedValidFrom(), leftBorderToVersioning.getVersionable().getValidTo(),
            entityLeftBorderToUpdate);
        versionedObjects.add(versionedLeftBorder);
      } else if (isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
          toVersioningList.get(0))) {
        //just update props
        ToVersioning leftBorderToVersioning = toVersioningList.get(0);
        Entity entityLeftBorderToUpdate = replaceEditedPropertiesWithCurrentProperties(
            vd.getEditedEntity(),
            leftBorderToVersioning.getEntity());
        VersionedObject versionedLeftBorder = buildVersionedObjectToUpdate(
            leftBorderToVersioning.getVersionable().getValidFrom(),
            leftBorderToVersioning.getVersionable().getValidTo(),
            entityLeftBorderToUpdate);
        versionedObjects.add(versionedLeftBorder);

      } else if (!isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(), toVersioningList)
          && !isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
          toVersioningList.get(0))) {
        applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd.getEditedValidFrom(),
            vd.getEditedEntity(), toVersioningList, versionedObjects);
      } else {
        throw new VersioningException(
            "Something went wrong. I'm not able to apply versioning on this scenario.");
      }
      if (isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)) {
        //update validTo and merge props
        ToVersioning rightBorderToVersioning = toVersioningList.get(
            toVersioningList.size() - 1);
        Entity entityRightBorderToUpdate = replaceEditedPropertiesWithCurrentProperties(
            vd.getEditedEntity(),
            rightBorderToVersioning.getEntity());
        VersionedObject versionedRightBorder = buildVersionedObjectToUpdate(
            rightBorderToVersioning.getVersionable().getValidFrom(), vd.getEditedValidTo(),
            entityRightBorderToUpdate);
        versionedObjects.add(versionedRightBorder);
      } else if (isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(), toVersioningList.get(
          toVersioningList.size() - 1))) {
        //just update props
        ToVersioning rightBorderToVersioning = toVersioningList.get(
            toVersioningList.size() - 1);
        Entity entityRightBorderToUpdate = replaceEditedPropertiesWithCurrentProperties(
            vd.getEditedEntity(),
            rightBorderToVersioning.getEntity());
        VersionedObject versionedRightBorder = buildVersionedObjectToUpdate(
            rightBorderToVersioning.getVersionable().getValidFrom(),
            rightBorderToVersioning.getVersionable()
                                   .getValidTo(),
            entityRightBorderToUpdate);
        versionedObjects.add(versionedRightBorder);

      } else if (!isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)
          && !isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(), toVersioningList.get(
          toVersioningList.size() - 1))) {
        applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd.getEditedValidTo(),
            vd.getEditedEntity(),
            toVersioningList, versionedObjects);
      } else {
        throw new VersioningException(
            "Something went wrong. I'm not able to apply versioning on this scenario.");
      }
      applyVersioningBetweenLeftAndRightBorder(vd.getEditedEntity(), toVersioningList,
          versionedObjects);
      return versionedObjects;
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private List<VersionedObject> applyVersioningWhenThereIsGapBetweenVersionsFound(
      LocalDate editedValidFrom, LocalDate editedValidTo,
      Versionable editedVersion,
      Versionable currentVersion, Entity editedEntity,
      List<ToVersioning> toVersioningList) {
    log.info("Matched multiple versions with gap");

    if (isOnlyValidToChanged(editedVersion, currentVersion)
        || areBothValidToAndValidFromChanged(editedVersion, currentVersion)) {
      List<VersionedObject> versionedObjects = new ArrayList<>();
      for (int i = 0; i < toVersioningList.size(); i++) {
        ToVersioning current = toVersioningList.get(i);

        if (hasNextVersion(toVersioningList, i)) {
          ToVersioning next = toVersioningList.get(i + 1);

          if (!areDatesSequential(current.getVersionable().getValidTo(),
              next.getVersionable().getValidFrom())) {
            log.info("Matched gap {} - {}", current.getVersionable().getValidTo(),
                next.getVersionable().getValidFrom());
            log.info("{}\n{}", current, next);

            if (isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(editedValidFrom,
                current)) {
              //1. current Version UPDATE
              //   current.validTo=editedValidTo-1
              VersionedObject versionedObjectToUpdate = buildVersionedObjectToUpdate(
                  current.getVersionable().getValidFrom(),
                  editedValidFrom.minusDays(1), current.getEntity());
              versionedObjects.add(versionedObjectToUpdate);
              //2. create new Version
              //  new.validFrom=editedValidFom
              //  new.validTo=next.validFrom-1
              //  merge properties
              Entity currentEntityToCreate = replaceEditedPropertiesWithCurrentProperties(
                  editedEntity,
                  current.getEntity());
              VersionedObject versionedObjectFillGap = buildVersionedObjectToCreate(
                  editedValidFrom,
                  next.getVersionable().getValidFrom().minusDays(1), currentEntityToCreate);
              versionedObjects.add(versionedObjectFillGap);
            }
            //1.case: perfect match editedValidFrom == current.validFrom
            //2.case: we are in the middle of multiple versions with gap
            // and we are matching the second item (e.g.) where:
            // editValidFrom < 2.validFrom && editedValidTo > 2.validTo
            // edited  |-------------------------------------------------|
            // current |--------------|    |---------------|    |---------------|
            //                1                     2                   3
            else if (isEditedValidFromExactOnTheLeftBorder(editedValidFrom, current) ||
                isCurrentVersionBetweenEditedValidFromAndEditedValidTo(editedValidFrom,
                    editedValidTo, current)) {
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
              throw new VersioningException(
                  "Something went wrong. I'm not able to apply versioning on this scenario.");
            }

          } else if (areDatesSequential(current.getVersionable().getValidTo(),
              next.getVersionable().getValidFrom())) {
            //versions current and next are sequential
            applyVersioningWhenThereIsGapNearToTheVersion(editedValidTo, editedEntity,
                current, versionedObjects);
          } else {
            throw new VersioningException(
                "Something went wrong. I'm not able to apply versioning on this scenario.");
          }
        } else if (!hasNextVersion(toVersioningList, i)) {
          //does not have next
          applyVersioningWhenThereIsGapNearToTheVersion(editedValidTo, editedEntity,
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

  private void applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(
      LocalDate editedValidFrom, Entity editedEntity,
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
  }


  private void applyVersioningWhenThereIsGapNearToTheVersion(LocalDate editedValidTo,
      Entity editedEntity,
      ToVersioning current, List<VersionedObject> versionedObjects) {
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


  private void applyVersioningBetweenLeftAndRightBorder(Entity editedEntity,
      List<ToVersioning> toVersioningList,
      List<VersionedObject> versionedObjects) {
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

  private void applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(
      LocalDate editedValidTo, Entity editedEntity,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    // 3. versions.get(versions.size()-1)
    //    validFrom = editedValidTo.plusDay(1)
    //    do not update properties
    //    VersioningAction = UPDATE

    //if validTo is after lastIndex0.getValidTo
    // split versions
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
  }


}
