package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ToiletVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ToiletVersionJsonProcessorTest {

  @Test
  public void shouldMapToReadModel() {
    ToiletVersion entity = ToiletVersion.builder()
        .id(1L)
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .additionalInformation("String")
        .designation("desig")
        .wheelchairToilet(StandardAttributeType.NO)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .build();

    ToiletVersionJsonProcessor processor = new ToiletVersionJsonProcessor();

    ToiletVersionModel expected = ReadToiletVersionModel.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .parentServicePointSloid("ch:1:sloid:112")
        .additionalInformation("String")
        .designation("desig")
        .wheelchairToilet(StandardAttributeType.NO)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .build();

    ReadToiletVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }
}
