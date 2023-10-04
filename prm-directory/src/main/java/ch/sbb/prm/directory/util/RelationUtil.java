package ch.sbb.prm.directory.util;

import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.enumeration.StepFreeAccessAttributeType;
import ch.sbb.prm.directory.enumeration.TactileVisualAttributeType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationUtil {

  public static RelationVersion buildReleaseVersion(BasePrmEntityVersion version,
      ReferencePointElementType referencePointElementType) {
    return RelationVersion.builder()
        .sloid(version.getSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .referencePointElementType(referencePointElementType)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .build();
  }

}
