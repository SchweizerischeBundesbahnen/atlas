package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.lidi.Subline;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class SublineJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() {
    Subline subline = Subline.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000:1")
        .mainlineSlnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .sublineType(SublineType.OPERATIONAL)
        .concessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .swissSublineNumber("r.01.1")
        .description("Linie 1a")
        .businessOrganisation("ch:1:sboid:10000011")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    ReadSublineVersionModelV2 expected = ReadSublineVersionModelV2.builder()
        .id(1L)
        .description("description")
        .slnid("ch:1:slnid:100000:1")
        .mainlineSlnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .sublineType(SublineType.OPERATIONAL)
        .sublineConcessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .swissSublineNumber("r.01.1")
        .description("Linie 1a")
        .businessOrganisation("ch:1:sboid:10000011")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .etagVersion(0)
        .build();

    ReadSublineVersionModelV2 result = new SublineJsonProcessor().process(subline);
    assertThat(result).isEqualTo(expected);
  }
}