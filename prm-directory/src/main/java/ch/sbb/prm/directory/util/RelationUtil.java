package ch.sbb.prm.directory.util;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.service.Relatable;
import jakarta.validation.constraints.NotEmpty;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationUtil {

  public static RelationVersion buildRelationVersion(List<? extends Relatable> relatableVersions,
      ReferencePointVersion referencePointVersion, ReferencePointElementType referencePointElementType) {
    return buildRelationVersion(relatableVersions.getFirst(), List.of(referencePointVersion), referencePointElementType);
  }

  public static RelationVersion buildRelationVersion(Relatable relatableVersion,
      List<@NotEmpty ReferencePointVersion> referencePoint,
      ReferencePointElementType referencePointElementType) {
    List<ReferencePointVersion> sortedReferencePoints = referencePoint.stream()
        .sorted(Comparator.comparing(ReferencePointVersion::getValidFrom)).toList();
    return RelationVersion.builder()
        .sloid(relatableVersion.getSloid())
        .number(relatableVersion.getNumber())
        .parentServicePointSloid(relatableVersion.getParentServicePointSloid())
        .validFrom(sortedReferencePoints.getFirst().getValidFrom())
        .validTo(sortedReferencePoints.getLast().getValidTo())
        .referencePointSloid(sortedReferencePoints.getFirst().getSloid())
        .referencePointElementType(referencePointElementType)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .build();
  }

}
