package ch.sbb.exportservice.job.lidi.ttfn.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.ttfn.entity.TimetableFieldNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimetableFieldNumberJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() {
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

    TimetableFieldNumberVersionModel expected = TimetableFieldNumberVersionModel.builder()
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
        .etagVersion(0)
        .build();

    TimetableFieldNumberVersionModel result = new TimetableFieldNumberJsonProcessor().process(timetableFieldNumber);
    assertThat(result).isEqualTo(expected);
  }
}