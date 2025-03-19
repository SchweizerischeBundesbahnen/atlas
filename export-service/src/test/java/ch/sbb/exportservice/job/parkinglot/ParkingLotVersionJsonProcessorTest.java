package ch.sbb.exportservice.job.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.parkinglot.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.parkinglot.ParkingLotVersionJsonProcessor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ParkingLotVersionJsonProcessorTest {

  @Test
  void shouldMapToReadModel() {
    ParkingLotVersion entity = ParkingLotVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("desig")
        .additionalInformation("add")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ParkingLotVersionJsonProcessor processor = new ParkingLotVersionJsonProcessor();

    ReadParkingLotVersionModel expected = ReadParkingLotVersionModel.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("desig")
        .additionalInformation("add")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ReadParkingLotVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }
}