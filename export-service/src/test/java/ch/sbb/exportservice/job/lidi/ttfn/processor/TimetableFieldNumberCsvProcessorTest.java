package ch.sbb.exportservice.job.lidi.ttfn.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.ttfn.entity.TimetableFieldNumber;
import ch.sbb.exportservice.job.lidi.ttfn.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.util.MapperUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimetableFieldNumberCsvProcessorTest {

  @Test
  void shouldMapToCsvCorrectly() {
    TimetableFieldNumber timetableFieldNumber = TimetableFieldNumber.builder()
        .id(1L)
        .description("description")
        .number("number")
        .ttfnid("ch:1:ttfnid:123")
        .swissTimetableFieldNumber("sttfn")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("ch:1:sboid:100000")
        .comment("comment")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    TimetableFieldNumberCsvModel expected = TimetableFieldNumberCsvModel.builder()
        .description("description")
        .number("number")
        .ttfnid("ch:1:ttfnid:123")
        .swissTimetableFieldNumber("sttfn")
        .status(Status.VALIDATED)
        .validFrom("2020-01-01")
        .validTo("2020-12-31")
        .businessOrganisation("ch:1:sboid:100000")
        .lineRelations("")
        .comment("comment")
        .creationTime(MapperUtil.LOCAL_DATE_FORMATTER.format(LocalDateTime.of(2024, 2, 7, 20, 0)))
        .editionTime(MapperUtil.LOCAL_DATE_FORMATTER.format(LocalDateTime.of(2024, 2, 7, 20, 0)))
        .build();

    TimetableFieldNumberCsvModel result = new TimetableFieldNumberCsvProcessor().process(timetableFieldNumber);
    assertThat(result).isEqualTo(expected);
  }
}