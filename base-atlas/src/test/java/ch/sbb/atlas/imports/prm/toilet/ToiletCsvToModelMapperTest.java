package ch.sbb.atlas.imports.prm.toilet;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.testdata.prm.ToiletCsvTestData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ToiletCsvToModelMapperTest {

  @Test
  void shouldMapCsvToCreateModelCorrectly() {
    //given
    ToiletCsvModel csvModel = ToiletCsvTestData.getCsvModel();
    ToiletVersionModel expected = ToiletVersionModel.builder()
        .sloid("ch:1:sloid:76646:0:17")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:76646")
        .additionalInformation("""
                Blindenquadrat und bodenmarkierter Haltebalken vorhanden,Tramlinie 7 in Fahrtrichtung ( Bümpliz ), Buslinie 12 in Fahrtrichtung Holligen ( Inselspital ) und ( Moonliner 6 /17 18 ) sind ebenfalls aktiv.
                """)
        .designation("Desc")
        .wheelchairToilet(StandardAttributeType.NO)
        .build();

    ToiletVersionModel result = ToiletCsvToModelMapper.toModel(csvModel);
    assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);
  }

}