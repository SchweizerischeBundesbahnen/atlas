package ch.sbb.exportservice.job.toilet;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.toilet.ToiletVersion;
import ch.sbb.exportservice.job.prm.toilet.ToiletVersionCsvProcessor;
import ch.sbb.exportservice.util.MapperUtil;
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
        .status(Status.REVOKED)
        .build();

    ToiletVersionCsvModel expected = ToiletVersionCsvModel.builder()
        .sloid("ch:1:sloid:112:23")
        .parentSloidServicePoint("ch:1:sloid:112")
        .parentNumberServicePoint(8500112)
        .designation("Haupteingang")
        .additionalInformation("Langer Text")
        .wheelchairToilet("NO")
        .validFrom(MapperUtil.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
        .validTo(MapperUtil.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(creationDate))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(editionDate))
        .status(Status.REVOKED)
        .build();

    ToiletVersionCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }
}