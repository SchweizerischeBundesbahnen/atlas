package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isThereGapBetweenVersions;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isVersionOverTheLeftBorder;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.isVersionOverTheRightBorder;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenNoEntityFound implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning wenn no entity found.");

    List<VersionedObject> versionedObjects = new ArrayList<>();

    ToVersioning rightBorderVersion = vd.getObjectsToVersioning()
                                        .get(vd.getObjectsToVersioning().size() - 1);
    if (isVersionOverTheRightBorder(rightBorderVersion, vd.getEditedValidFrom())) {
      log.info("Match over the right border.");
      applyVersioningOverTheBorder(vd, rightBorderVersion, versionedObjects);
      return versionedObjects;
    }
    ToVersioning leftBorderVersion = vd.getObjectsToVersioning().get(0);
    if (isVersionOverTheLeftBorder(leftBorderVersion, vd.getEditedValidTo())) {
      log.info("Match over the left border.");
      applyVersioningOverTheBorder(vd, leftBorderVersion, versionedObjects);
      return versionedObjects;
    }
    if (isThereGapBetweenVersions(vd.getObjectsToVersioning())) {
      log.info("Match a gap between two objects.");
      return applyVersioningOnTheGap(vd, versionedObjects);
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private void applyVersioningOverTheBorder(VersioningData vd,
      ToVersioning borderVersion, List<VersionedObject> versionedObjects) {
    Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(),
        borderVersion.getEntity());
    VersionedObject versionedObject = buildVersionedObjectToCreate(vd.getEditedValidFrom(),
        vd.getEditedValidTo(), entityToAdd);
    versionedObjects.add(versionedObject);
  }

  private List<VersionedObject> applyVersioningOnTheGap(VersioningData vd,
      List<VersionedObject> versionedObjects) {
    ToVersioning toVersioningBeforeGap = getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        vd.getEditedValidFrom(), vd.getEditedValidTo(), vd.getObjectsToVersioning());
    if (toVersioningBeforeGap == null) {
      throw new VersioningException(
          "Something went wrong. I'm not able to apply versioning on this scenario.");
    }
    applyVersioningOverTheBorder(vd, toVersioningBeforeGap, versionedObjects);
    return versionedObjects;
  }

}
