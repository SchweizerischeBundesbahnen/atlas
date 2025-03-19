package ch.sbb.exportservice.job.lidi.line.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class LineJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() {
    Line line = Line.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    LineVersionModelV2 expected = LineVersionModelV2.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .etagVersion(0)
        .build();

    LineVersionModelV2 result = new LineJsonProcessor().process(line);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

}