package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.enumeration.*;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationVersionMapperTest {

    @Test
    void shouldMapToModelCorrectly() {
        // Given
        RelationVersion relationVersion = RelationTestData.getRelation("ch:1:sloid:12345", "ch:1:sloid:12345:1",ReferencePointElementType.PLATFORM);

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
                .build();

        assertThat(relationVersionModel).usingRecursiveComparison().isEqualTo(expected);
    }
}
