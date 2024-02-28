package ch.sbb.atlas.versioning.version;

import static ch.sbb.atlas.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.atlas.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.atlas.versioning.version.VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions;
import static ch.sbb.atlas.versioning.version.VersioningHelper.isThereGapBetweenVersions;
import static ch.sbb.atlas.versioning.version.VersioningHelper.isVersionOverTheLeftBorder;
import static ch.sbb.atlas.versioning.version.VersioningHelper.isVersionOverTheRightBorder;

import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenNoEntityFound implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning when no entity found.");
    if (isVersionOverTheRightBorder(vd)) {
      log.info("Match over the right border.");
      return applyVersioningOverTheBorder(vd, vd.getTargetVersion());
    }
    if (isVersionOverTheLeftBorder(vd)) {
      log.info("Match over the left border.");
      return applyVersioningOverTheBorder(vd, vd.getTargetVersion());
    }
    if (isThereGapBetweenVersions(vd.getObjectsToVersioning())) {
      log.info("Match a gap between two objects.");
      return applyVersioningOnTheGap(vd);
    }
    throw new VersioningException(
        "Something went wrong. I'm not able to apply versioning on this scenario.");
  }

  private List<VersionedObject> applyVersioningOverTheBorder(VersioningData vd,
      ToVersioning borderVersion) {
    Entity entityToAdd = replaceEditedPropertiesWithCurrentProperties(
        vd.getEditedEntity(),
        borderVersion.getEntity());
    VersionedObject versionedObject = buildVersionedObjectToCreate(vd.getEditedValidFrom(),
        vd.getEditedValidTo(), entityToAdd);
    return List.of(versionedObject);
  }

  private List<VersionedObject> applyVersioningOnTheGap(VersioningData vd) {
    ToVersioning toVersioningBeforeGap = getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        vd.getEditedValidFrom(), vd.getEditedValidTo(), vd.getObjectsToVersioning());
    if (toVersioningBeforeGap == null) {
      throw new VersioningException();
    }
    return applyVersioningOverTheBorder(vd, toVersioningBeforeGap);
  }

}
