package ch.sbb.atlas.imports.prm.referencepoint;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.testdata.prm.ReferencePointCsvTestData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReferencePointCsvToModelMapperTest {

    @Test
    void shouldMapCsvToCreateModelCorrectly() {
        //given
        ReferencePointCsvModel csvModel = ReferencePointCsvTestData.getCsvModel();
        ReferencePointVersionModel expected = ReferencePointVersionModel.builder()
                .sloid("ch:1:sloid:294:787306")
                .parentServicePointSloid("ch:1:sloid:294")
                .designation("Perron BTH")
                .mainReferencePoint(true)
                .additionalInformation("Additional Info Example")
                .referencePointType(ReferencePointAttributeType.PLATFORM)
                .validFrom(LocalDate.of(2020, 8, 25))
                .validTo(LocalDate.of(2025, 12, 31))
                .build();

        ReferencePointVersionModel result = ReferencePointCsvToModelMapper.toModel(csvModel);
        assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);
    }
}
