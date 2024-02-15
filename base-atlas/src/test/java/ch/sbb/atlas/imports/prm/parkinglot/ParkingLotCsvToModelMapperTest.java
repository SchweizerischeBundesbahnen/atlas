package ch.sbb.atlas.imports.prm.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.testdata.prm.ParkingLotCsvTestData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ParkingLotCsvToModelMapperTest {

  @Test
  void shouldMapCsvToCreateModelCorrectly() {
    ParkingLotVersionModel expected = ParkingLotVersionModel.builder()
        .sloid("ch:1:sloid:294:787306")
        .parentServicePointSloid("ch:1:sloid:294")
        .additionalInformation("Additional Info Example")
        .placesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .validFrom(LocalDate.of(2020, 8, 25))
        .validTo(LocalDate.of(2025, 12, 31))
        .creationDate(LocalDateTime.now())
        .editionDate(LocalDateTime.now())
        .build();
    ParkingLotCsvModel csvModel = ParkingLotCsvTestData.getCsvModel();

    ParkingLotVersionModel result = ParkingLotCsvToModelMapper.toModel(csvModel);
    assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);

  }
}