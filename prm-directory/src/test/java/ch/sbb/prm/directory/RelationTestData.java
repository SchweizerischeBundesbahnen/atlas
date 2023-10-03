package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.enumeration.StepFreeAccessAttributeType;
import ch.sbb.prm.directory.enumeration.TactileVisualAttributeType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationTestData {

  public static RelationVersion getRelation(String parentServicePointSloid, String sloid,
      ReferencePointElementType referencePointElementType) {

    return RelationVersion.builder()
        .sloid(sloid)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid(parentServicePointSloid)
        .referencePointElementType(referencePointElementType)
        .contrastingAreas(StandardAttributeType.YES)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .stepFreeAccess(StepFreeAccessAttributeType.NO)
        .build();
  }

}
