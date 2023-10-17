package ch.sbb.prm.directory.util;

import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.prm.directory.service.Relatable;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationUtil {

  public static RelationVersion buildRelationVersion(Relatable version,
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
