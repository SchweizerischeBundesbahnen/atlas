package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReferencePointVersionCsvProcessorTest {

  private final ReferencePointVersionCsvProcessor processor = new ReferencePointVersionCsvProcessor();

  @Test
  void shouldMapToCsvModel() {
    ReferencePointVersion entity = ReferencePointVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .designation("Haupteingang")
        .mainReferencePoint(true)
        .additionalInformation("""
            Langer
            Text""")
        .referencePointType(ReferencePointAttributeType.MAIN_STATION_ENTRANCE)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.now())
        .editionDate(LocalDateTime.now())
        .build();

    ReferencePointVersionCsvModel expected = ReferencePointVersionCsvModel.builder()
        .sloid("ch:1:sloid:112:23")
        .parentSloidServicePoint("ch:1:sloid:112")
        .parentNumberServicePoint(8500112)
        .designation("Haupteingang")
        .mainReferencePoint(true)
        .additionalInformation("Langer Text")
        .rpType("MAIN_STATION_ENTRANCE")
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(LocalDateTime.now()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(LocalDateTime.now()))
        .build();

    ReferencePointVersionCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }
}