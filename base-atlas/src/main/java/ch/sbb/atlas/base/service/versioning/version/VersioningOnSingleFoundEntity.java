package ch.sbb.atlas.base.service.versioning.version;

import static ch.sbb.atlas.base.service.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.atlas.base.service.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.atlas.base.service.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.arePropertiesEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isCurrentVersionFirstVersion;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isEditedValidFromOverTheLeftBorderAndEndsWithin;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isEditedValidToAfterTheRightBorderAndValidFromNotEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnBeginningOfVersionAndEndingWithin;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnTheRightBorderAndValidToIsOnOrOverTheBorder;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnlyValidFromEditedAndPropertiesAreNotEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnlyValidToEditedAndPropertiesAreEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isOnlyValidToEditedAndPropertiesAreNotEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isSingularVersionAndPropertiesAreNotEdited;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isVersionOverTheLeftAndTheRightBorder;

import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import ch.sbb.atlas.base.service.versioning.model.Entity;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningOnSingleFoundEntity implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning on single found entity.");
    ToVersioning toVersioning = vd.getSingleFoundObjectToVersioning();
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isSingularVersionAndPropertiesAreNotEdited(vd)) {
      return applyVersioningOnSingularVersionAndPropertiesAreNotEdited(vd, toVersioning);
    }
    if (isEditedVersionInTheMiddleOfCurrentEntity(vd.getEditedValidFrom(), vd.getEditedValidTo(),
        toVersioning)) {
      List<VersionedObject> versionedObjectsInTheMiddleOfAnExistingEntity =
          applyVersioningInTheMiddleOfAnExistingEntity(vd, toVersioning);
      versionedObjects.addAll(versionedObjectsInTheMiddleOfAnExistingEntity);
      return versionedObjects;
    }
    if (isVersionOverTheLeftAndTheRightBorder(vd) && arePropertiesEdited(vd)) {
      VersionedObject versionedObject = stretchVersion(vd, toVersioning);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }
    List<VersionedObject> versionedObjectsOnTheBorder =
        applyVersioningOnTheBorderOfSingleFoundEntity(vd, toVersioning);
    versionedObjects.addAll(versionedObjectsOnTheBorder);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningInTheMiddleOfAnExistingEntity(VersioningData vd,
      ToVersioning toVersioning) {
    log.info("Found in the middle of an existing.");
    if (isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(toVersioning, vd)) {
      log.info("Scenario 14m not supported");
      throw new VersioningException();
    }
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
    return buildVersionedObjectToUpdate(toVersioning.getValidFrom(),
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
    LocalDate toAddAtEndValidTo = toVersioning.getValidTo();
    return buildVersionedObjectToCreate(toAddAtEndValidFrom, toAddAtEndValidTo,
        toVersioning.getEntity());
  }

  private List<VersionedObject> applyVersioningOnTheBorderOfSingleFoundEntity(VersioningData vd,
      ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();

    if (isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(vd, toVersioning)) {
      log.info("Found on the left border, "
          + "editedValidFrom is before current validFrom and validTo is not edited.");
      // update validFrom=editedValidFrom and merge properties
      VersionedObject versionedObject =
          shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }
    if (isOnlyValidToEditedAndPropertiesAreNotEdited(vd)) {
      log.info("Found on the right border, validTo is edited, no properties are edited.");
      // update validTo=editedValidTo and merge properties
      VersionedObject versionedObject =
          shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning);
      versionedObjects.add(versionedObject);
      return versionedObjects;
    }
    if (isOnlyValidToEditedAndPropertiesAreEdited(vd)) {
      return applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(vd, toVersioning);
    }
    if (isOnlyValidFromEditedAndPropertiesAreNotEdited(vd)) {
      return applyVersioningOnTheRightBorderWhenValidFromIsEditedAndPropertiesAreNotEdited(vd,
          toVersioning);
    }
    if (isOnTheRightBorderAndValidToIsOnOrOverTheBorder(vd, toVersioning)) {
      return applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(vd, toVersioning);
    }
    if (isOnBeginningOfVersionAndEndingWithin(vd, toVersioning)) {
      return applyVersioningOnBeginningOfVersionAndEndingWithin(vd, toVersioning);
    }
    if (isEditedValidFromOverTheLeftBorderAndEndsWithin(vd)) {
      return applyVersioningOnTheLeftBorderWhenEditedEntityIsOnOrOverTheBorder(vd, toVersioning);
    }
    throw new VersioningException();
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenValidFromIsEditedAndPropertiesAreNotEdited(
      VersioningData vd, ToVersioning toVersioning) {
    if (isCurrentVersionFirstVersion(vd)) {
      log.info("Found on the right border, validFrom is edited, properties are not edited.");
      return Collections.singletonList(
          shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning));
    }
    throw new VersioningException();
  }

  private List<VersionedObject> applyVersioningOnSingularVersionAndPropertiesAreNotEdited(
      VersioningData vd, ToVersioning toVersioning) {
    log.info("Found single version, properties are not edited.");
    return Collections.singletonList(
        shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning));
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenValidToAndPropertiesAreEdited(
      VersioningData vd, ToVersioning toVersioning) {
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (isEditedValidToAfterTheRightBorderAndValidFromNotEdited(vd, toVersioning)) {
      log.info("Found on the right border, validTo is after current validTo, properties edited.");
      // update validTo=editedValidTo and update properties
      VersionedObject versionedObject =
          shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning);
      versionedObjects.add(versionedObject);
    } else {
      log.info("Found version to split on the right border, validTo and properties edited.");
      // update validTo=editedValidTo and update properties
      VersionedObject versionedObject =
          shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(vd, toVersioning);
      versionedObjects.add(versionedObject);
      // add new version after the current edited version
      VersionedObject versionedObjectCreated =
          addNewVersionOnTheCurrentRightBorder(vd, toVersioning);
      versionedObjects.add(versionedObjectCreated);
    }
    return versionedObjects;
  }

  private VersionedObject addNewVersionOnTheCurrentRightBorder(VersioningData vd,
      ToVersioning toVersioning) {
    return buildVersionedObjectToCreate(vd.getEditedValidTo().plusDays(1),
        toVersioning.getValidTo(), toVersioning.getEntity());
  }

  private List<VersionedObject> applyVersioningOnTheRightBorderWhenEditedEntityIsOnOrOverTheBorder(
      VersioningData vd, ToVersioning toVersioning) {
    log.info("Found version to split on or over the right border, validTo and properties edited.");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (vd.getEditedValidFrom().equals(toVersioning.getValidFrom())) {
      // update current version: validTo = editedValidTo and update properties
      VersionedObject shortenOrLengthenVersion = shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(
          vd,
          toVersioning);
      versionedObjects.add(shortenOrLengthenVersion);
      return versionedObjects;
    } else if (vd.getEditedValidFrom().isAfter(toVersioning.getValidFrom())) {
      // update current version: validTo = editedValidFrom.minusDay(1),do not update properties
      VersionedObject shortenRightVersion = shortenRightVersion(vd, toVersioning);
      versionedObjects.add(shortenRightVersion);

      // Create new version: validFrom = editedValidFrom, validTo = versions.get(0).getValidTo(), update properties
      VersionedObject newVersionAfterTheRightBorder = addNewVersionAfterTheRightBorder(vd,
          toVersioning);
      versionedObjects.add(newVersionAfterTheRightBorder);
      return versionedObjects;
    }
    throw new VersioningException();
  }

  private List<VersionedObject> applyVersioningOnTheLeftBorderWhenEditedEntityIsOnOrOverTheBorder(
      VersioningData vd, ToVersioning toVersioning) {
    log.info("Found version to split on or over the left border, validFrom and properties edited.");
    List<VersionedObject> versionedObjects = new ArrayList<>();
    VersionedObject shortenedVersion = buildVersionedObjectToUpdate(
        vd.getEditedValidTo().plusDays(1),
        toVersioning.getValidTo(),
        toVersioning.getEntity());
    versionedObjects.add(shortenedVersion);

    VersionedObject newVersionBeforeTheLeftBorder = addNewVersionAfterTheRightBorder(vd,
        toVersioning);
    versionedObjects.add(newVersionBeforeTheLeftBorder);
    return versionedObjects;
  }

  private List<VersionedObject> applyVersioningOnBeginningOfVersionAndEndingWithin(
      VersioningData vd, ToVersioning toVersioning) {
    log.info(
        "Found version on the beginning of a version and ending within, validTo and properties edited.");
    List<VersionedObject> versionedObjects = new ArrayList<>();

    // Add Version on edited part
    versionedObjects.add(
        buildVersionedObjectToCreate(vd.getEditedValidFrom(), vd.getEditedValidTo(),
            replaceEditedPropertiesWithCurrentProperties(
                vd.getEditedEntity(),
                toVersioning.getEntity())));

    // Shorten existing on left side
    versionedObjects.add(buildVersionedObjectToUpdate(vd.getEditedValidTo().plusDays(1),
        toVersioning.getValidTo(),
        toVersioning.getEntity()));
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
    return buildVersionedObjectToUpdate(toVersioning.getValidFrom(),
        vd.getEditedValidFrom().minusDays(1),
        toVersioning.getEntity());
  }

  private VersionedObject stretchVersion(VersioningData vd, ToVersioning toVersioning) {
    Entity entity = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());
    return buildVersionedObjectToUpdate(vd.getEditedValidFrom(),
        vd.getEditedValidTo(), entity);
  }

  private VersionedObject shortenOrLengthenVersionAndUpdatePropertiesOnTheBorder(VersioningData vd,
      ToVersioning toVersioning) {
    Entity entity = replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());
    return buildVersionedObjectToUpdate(vd.getEditedValidFrom(), vd.getEditedValidTo(), entity);
  }

}
