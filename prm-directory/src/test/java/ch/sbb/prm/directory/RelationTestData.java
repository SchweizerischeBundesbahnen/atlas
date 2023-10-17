package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.RelationVersion.RelationVersionBuilder;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
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

  public static RelationVersionBuilder<?, ?> builderVersion1(){
    return RelationVersion.builder()
        .sloid("ch:1:sloid:123456:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:123456")
        .referencePointElementType(ReferencePointElementType.PLATFORM)
        .contrastingAreas(StandardAttributeType.YES)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .stepFreeAccess(StepFreeAccessAttributeType.NO);
  }

  public static RelationVersionBuilder<?, ?> builderVersion2(){
    return RelationVersion.builder()
        .sloid("ch:1:sloid:123456:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:123456")
        .referencePointElementType(ReferencePointElementType.PLATFORM)
        .contrastingAreas(StandardAttributeType.NO)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .stepFreeAccess(StepFreeAccessAttributeType.NO);
  }

  public static RelationVersionBuilder<?, ?> builderVersion3(){
    return RelationVersion.builder()
        .sloid("ch:1:sloid:123456:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:123456")
        .referencePointElementType(ReferencePointElementType.PLATFORM)
        .contrastingAreas(StandardAttributeType.NO)
        .tactileVisualMarks(TactileVisualAttributeType.NOT_APPLICABLE)
        .stepFreeAccess(StepFreeAccessAttributeType.NO);
  }

}
