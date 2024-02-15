package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ToiletVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ToiletVersionCsvProcessorTest {

  private final ToiletVersionCsvProcessor processor = new ToiletVersionCsvProcessor();

  @Test
  void shouldMapToCsvModel() {
    LocalDateTime creationDate = LocalDateTime.now();
    LocalDateTime editionDate = LocalDateTime.now();
    ToiletVersion entity = ToiletVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("Haupteingang")
        .additionalInformation("Langer Text")
        .wheelchairToilet(StandardAttributeType.NO)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(creationDate)
        .editionDate(editionDate)
        .build();

    ToiletVersionCsvModel expected = ToiletVersionCsvModel.builder()
        .sloid("ch:1:sloid:112:23")
        .parentSloidServicePoint("ch:1:sloid:112")
        .parentNumberServicePoint(8500112)
        .designation("Haupteingang")
        .additionalInformation("Langer Text")
        .wheelchairToilet("NO")
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(creationDate))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(editionDate))
        .build();

    ToiletVersionCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }
}