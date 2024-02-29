package ch.sbb.atlas.imports.prm.relation;

import ch.sbb.atlas.api.prm.enumeration.*;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.testdata.prm.RelationCsvTestData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RelationCsvToModelMapperTest {

    @Test
    void shouldMapCsvToCreateModelCorrectly() {
        //given
        RelationCsvModel csvModel = RelationCsvTestData.getCsvModel();
        RelationVersionModel expected = RelationVersionModel.builder()
                .sloid("ch:1:sloid:294:787306")
                .referencePointElementType(ReferencePointElementType.PLATFORM)
                .parentServicePointSloid("ch:1:sloid:294")
                .referencePointSloid("ch:1:sloid:294:1")
                .contrastingAreas(StandardAttributeType.YES)
                .stepFreeAccess(StepFreeAccessAttributeType.YES)
                .tactileVisualMarks(TactileVisualAttributeType.YES)
                .validFrom(LocalDate.of(2020, 8, 25))
                .validTo(LocalDate.of(2025, 12, 31))
                .creationDate(LocalDateTime.now())
                .editionDate(LocalDateTime.now())
                .build();

        RelationVersionModel result = RelationCsvToModelMapper.toModel(csvModel);
        assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);
    }
}
