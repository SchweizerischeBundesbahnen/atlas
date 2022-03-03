package ch.sbb.atlas.versioning.version;

import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningOverMultipleFoundEntities implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning over multiple found entities.");
    List<ToVersioning> toVersioningList = vd.getObjectToVersioningFound();

    GapFiller.fillGapsInToVersioning(vd);
    if (VersioningHelper.isEditedVersionExactMatchingMultipleEntities(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningExactMatchingMultipleVersions(vd, toVersioningList);
    }
    if (VersioningHelper.isBetweenMultipleVersionsAndOverTheBorders(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningBetweenMultipleEntitiesOverTheBorders(vd, toVersioningList);
    }
    if (VersioningHelper.isBetweenMultipleVersionsAndStartsOnABorder(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningBetweenMultipleEntitiesAndStartsOnABorder(vd, toVersioningList);
    }
    if (VersioningHelper.isBetweenMultipleVersionsAndEndsOnABorder(vd.getEditedValidFrom(),
        vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningBetweenMultipleEntitiesAndEndsOnABorder(vd, toVersioningList);
    }
    if (VersioningHelper.isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(),
        toVersioningList)
        || VersioningHelper.isEditedValidToOverTheRightBorder(vd.getEditedValidTo(),
        toVersioningList)) {
      return applyVersioningOverTheBorders(vd, toVersioningList);
    }
    throw new VersioningException();
  }

  private List<VersionedObject> applyVersioningExactMatchingMultipleVersions(VersioningData vd,
      List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    log.info(
        "Matched multiple versions on the borders: editedValidFrom is equal to the first matched version validFrom"
            + " and the editedValidTo is equal to the last matched version ValidTo.");
    for (ToVersioning toVersioning : toVersioningList) {
      // update properties all versions
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          toVersioning.getValidFrom(), toVersioning.getValidTo(), toVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedObject);
    }
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningOverTheBorders(VersioningData vd,
      List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    applyVersioningToTheLeftBorder(vd, toVersioningList, versionedObjects);
    applyVersioningToTheRightBorder(vd, toVersioningList, versionedObjects);
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private void applyVersioningToTheLeftBorder(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    ToVersioning leftBorderToVersioning = toVersioningList.get(0);
    if (VersioningHelper.isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(),
        toVersioningList)) {
      //update validTo and merge props
      VersionedObject versionedLeftBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          vd.getEditedValidFrom(), leftBorderToVersioning.getValidTo(), leftBorderToVersioning,
          vd.getEditedEntity());
      versionedObjects.add(versionedLeftBorder);
    } else if (VersioningHelper.isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
        leftBorderToVersioning)) {
      //just update props
      VersionedObject versionedLeftBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          leftBorderToVersioning.getValidFrom(), leftBorderToVersioning.getValidTo(),
          leftBorderToVersioning, vd.getEditedEntity());
      versionedObjects.add(versionedLeftBorder);
    } else if (!VersioningHelper.isEditedValidFromOverTheLeftBorder(vd.getEditedValidFrom(),
        toVersioningList)
        && !VersioningHelper.isEditedValidFromExactOnTheLeftBorder(vd.getEditedValidFrom(),
        leftBorderToVersioning)) {
      applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd, toVersioningList,
          versionedObjects);
    } else {
      throw new VersioningException();
    }
  }

  private void applyVersioningToTheRightBorder(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    ToVersioning rightBorderToVersioning = toVersioningList.get(
        toVersioningList.size() - 1);
    if (VersioningHelper.isEditedValidToOverTheRightBorder(vd.getEditedValidTo(),
        toVersioningList)) {
      //update validTo and merge props
      VersionedObject versionedRightBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          rightBorderToVersioning.getValidFrom(), vd.getEditedValidTo(),
          rightBorderToVersioning, vd.getEditedEntity());
      versionedObjects.add(versionedRightBorder);
    } else if (VersioningHelper.isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(),
        rightBorderToVersioning)) {
      //just update props
      VersionedObject versionedRightBorder = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          rightBorderToVersioning.getValidFrom(), rightBorderToVersioning.getValidTo(),
          rightBorderToVersioning, vd.getEditedEntity());
      versionedObjects.add(versionedRightBorder);
    } else if (
        !VersioningHelper.isEditedValidToOverTheRightBorder(vd.getEditedValidTo(), toVersioningList)
            && !VersioningHelper.isEditedValidToExactOnTheRightBorder(vd.getEditedValidTo(),
            rightBorderToVersioning)) {
      applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd, toVersioningList,
          versionedObjects);
    } else {
      throw new VersioningException();
    }
  }

  private List<VersionedObject> applyVersioningBetweenMultipleEntitiesOverTheBorders(
      VersioningData vd, List<ToVersioning> toVersioningList) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    log.info("Matched multiple versions over the borders.");
    applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd, toVersioningList,
        versionedObjects);
    applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd, toVersioningList,
        versionedObjects);
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningBetweenMultipleEntitiesAndStartsOnABorder(
      VersioningData vd, List<ToVersioning> toVersioningList) {
    log.info("Starts on validFrom (Szenario 13c)");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    ToVersioning firstVersion = toVersioningList.get(0);
    versionedObjects.add(
        shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(firstVersion.getValidFrom(),
            firstVersion.getValidTo(), firstVersion, vd.getEditedEntity()));
    applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(vd, toVersioningList,
        versionedObjects);
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningBetweenMultipleEntitiesAndEndsOnABorder(
      VersioningData vd, List<ToVersioning> toVersioningList) {
    log.info("Ends on validTo (Szenario 13d)");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(vd, toVersioningList,
        versionedObjects);
    ToVersioning lastVersion = toVersioningList.get(toVersioningList.size() - 1);
    versionedObjects.add(
        shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(lastVersion.getValidFrom(),
            lastVersion.getValidTo(), lastVersion, vd.getEditedEntity()));
    applyVersioningBetweenLeftAndRightBorder(vd, toVersioningList, versionedObjects);
    return versionedObjects;
  }

  private void applyVersioningOnLeftBorderWhenValidFromIsAfterCurrentValidFrom(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    log.info("Found version to split on the left border.");
    // update version: validTo = editedValidFrom.minusDay(1) and no properties update
    ToVersioning leftBorderVersioning = toVersioningList.get(0);
    VersionedObject versionedLeftBorder =
        shortenOrLengthenVersionOnLeftBorder(vd, leftBorderVersioning);
    versionedObjects.add(versionedLeftBorder);
    // add new version: validTo = versions.get(0).getValidTo() and update properties
    VersionedObject versionedObjectAfterLeftBorder =
        addNewVersionAfterLeftBorder(vd, leftBorderVersioning);
    versionedObjects.add(versionedObjectAfterLeftBorder);
  }


  private void applyVersioningBetweenLeftAndRightBorder(VersioningData vd,
      List<ToVersioning> toVersioningList,
      List<VersionedObject> versionedObjects) {
    // update properties for each version between index = 1 and index = version.size()-1
    for (int i = 1; i < toVersioningList.size() - 1; i++) {
      ToVersioning toVersioning = toVersioningList.get(i);
      VersionedObject versionedObject = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          toVersioning.getValidFrom(), toVersioning.getValidTo(), toVersioning, vd.getEditedEntity()
      );
      versionedObjects.add(versionedObject);
    }
  }

  private void applyVersioningOnTheLeftBorderWhenValidToIsBeforeCurrentValidTo(VersioningData vd,
      List<ToVersioning> toVersioningList, List<VersionedObject> versionedObjects) {
    // split versions

    //if validTo is after lastIndex0.getValidTo
    ToVersioning leftBorderToVersioning = toVersioningList.get(
        toVersioningList.size() - 1);

    //update version: validFrom=editedValidTo+1 no properties update
    VersionedObject versionedObjectLastIndex = updateCurrentVersion(leftBorderToVersioning,
        vd.getEditedValidTo().plusDays(1), leftBorderToVersioning.getValidTo());
    versionedObjects.add(versionedObjectLastIndex);
    // create new version: validFrom = editedValidTo, validTo=editedValidTo, update properties
    VersionedObject versionedObjectBeforeLastIndex = addNewVersionBeforeCurrentVersion(
        vd.getEditedValidTo(), vd.getEditedEntity(), leftBorderToVersioning);
    versionedObjects.add(versionedObjectBeforeLastIndex);
  }

  private VersionedObject shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
      LocalDate editedValidFrom, LocalDate validTo,
      ToVersioning toVersioning,
      Entity editedEntity) {
    Entity entityLeftBorderToUpdate = Entity.replaceEditedPropertiesWithCurrentProperties(
        editedEntity, toVersioning.getEntity());
    return VersionedObject.buildVersionedObjectToUpdate(editedValidFrom, validTo,
        entityLeftBorderToUpdate);
  }

  private VersionedObject addNewVersionAfterLeftBorder(VersioningData vd,
      ToVersioning leftBorderVersioning) {
    Entity entityToAddAfterLeftBorder = Entity.replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(), leftBorderVersioning.getEntity());
    return VersionedObject.buildVersionedObjectToCreate(vd.getEditedValidFrom(),
        leftBorderVersioning.getValidTo(),
        entityToAddAfterLeftBorder);
  }

  private VersionedObject shortenOrLengthenVersionOnLeftBorder(VersioningData vd,
      ToVersioning leftBorderToVersioning) {
    LocalDate leftBorderToVersioningValidTo = vd.getEditedValidFrom().minusDays(1);
    return VersionedObject.buildVersionedObjectToUpdate(leftBorderToVersioning.getValidFrom(),
        leftBorderToVersioningValidTo, leftBorderToVersioning.getEntity());
  }

  private VersionedObject addNewVersionBeforeCurrentVersion(LocalDate editedValidTo,
      Entity editedEntity,
      ToVersioning current) {
    Entity entityToAdd = Entity.replaceEditedPropertiesWithCurrentProperties(editedEntity,
        current.getEntity());
    return VersionedObject.buildVersionedObjectToCreate(current.getValidFrom(), editedValidTo,
        entityToAdd);
  }

  private VersionedObject updateCurrentVersion(ToVersioning current, LocalDate localDate,
      LocalDate validTo) {
    return VersionedObject.buildVersionedObjectToUpdate(localDate, validTo, current.getEntity());
  }

}
