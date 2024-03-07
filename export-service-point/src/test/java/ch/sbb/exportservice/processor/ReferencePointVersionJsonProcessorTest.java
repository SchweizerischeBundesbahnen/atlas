package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.prm.ReferencePointVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReferencePointVersionJsonProcessorTest {

  @Test
   void shouldMapToReadModel() {
    ReferencePointVersion entity = ReferencePointVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("desig")
        .additionalInformation("add")
        .mainReferencePoint(true)
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ReferencePointVersionJsonProcessor processor = new ReferencePointVersionJsonProcessor();

    ReadReferencePointVersionModel expected = ReadReferencePointVersionModel.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("desig")
        .additionalInformation("add")
        .mainReferencePoint(true)
        .referencePointType(ReferencePointAttributeType.PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ReferencePointVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

}