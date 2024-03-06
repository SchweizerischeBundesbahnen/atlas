package ch.sbb.prm.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RelationVersionMapperTest {

  @Test
  void shouldMapToModelCorrectly() {
    // Given
    RelationVersion relationVersion = RelationTestData.getRelation("ch:1:sloid:12345", "ch:1:sloid:12345:1",
        ReferencePointElementType.PLATFORM);

    // When
    ReadRelationVersionModel relationVersionModel = RelationVersionMapper.toModel(relationVersion);

    ReadRelationVersionModel expected = ReadRelationVersionModel
        .builder()
        .elementSloid("ch:1:sloid:12345:1")
        .contrastingAreas(StandardAttributeType.YES)
        .tactileVisualMarks(TactileVisualAttributeType.YES)
        .stepFreeAccess(StepFreeAccessAttributeType.NO)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .referencePointElementType(ReferencePointElementType.PLATFORM)
        .referencePointSloid("ch:1:sloid:123456")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .status(Status.VALIDATED)
        .build();

    assertThat(relationVersionModel).usingRecursiveComparison().isEqualTo(expected);
  }
}
