package ch.sbb.atlas.base.service.versioning.version;

import static ch.sbb.atlas.base.service.versioning.model.Entity.replaceEditedPropertiesWithCurrentProperties;
import static ch.sbb.atlas.base.service.versioning.model.VersionedObject.buildVersionedObjectToCreate;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isThereGapBetweenVersions;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isVersionOverTheLeftBorder;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.isVersionOverTheRightBorder;

import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import ch.sbb.atlas.base.service.versioning.model.Entity;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenNoEntityFound implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning wenn no entity found.");

    ToVersioning rightBorderVersion = vd.getObjectsToVersioning()
                                        .get(vd.getObjectsToVersioning().size() - 1);
    if (isVersionOverTheRightBorder(rightBorderVersion, vd.getEditedValidFrom())) {
      log.info("Match over the right border.");
      return applyVersioningOverTheBorder(vd, rightBorderVersion);
    }
    ToVersioning leftBorderVersion = vd.getObjectsToVersioning().get(0);
    if (isVersionOverTheLeftBorder(leftBorderVersion, vd.getEditedValidTo())) {
      log.info("Match over the left border.");
      return applyVersioningOverTheBorder(vd, leftBorderVersion);
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
