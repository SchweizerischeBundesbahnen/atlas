package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LineCreateCsvModelTest {

  @Test
  void shouldBeValidLineCreateCsvModel() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .linienId("320")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .shortNumber("EX")
        .longName("Bernina Express")
        .businessOrganisation("ch:1:sboid:100053")
        .comment("Bernina Express / Konzessionsrecht ist nur für den schweizerischen Linienabschnitt gültig")
        .build();
    assertThat(lineCreateModel.validate()).isEmpty();
  }

  @Test
  void shouldBeValidMinimalLineCreateCsvModel() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateModel.validate()).isEmpty();
  }

  @Test
  void shouldReportMissingValidFrom() {
    LineCreateCsvModel lineCreateCsvModel = LineCreateCsvModel.builder()
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateCsvModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidTo() {
    LineCreateCsvModel lineCreateCsvModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateCsvModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingLineType() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingDescription() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingNumber() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingOfferCategory() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .businessOrganisation("ch:1:sboid:100053")
        .build();
    assertThat(lineCreateModel.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingBusinessOrganisation() {
    LineCreateCsvModel lineCreateModel = LineCreateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .build();
    assertThat(lineCreateModel.validate()).hasSize(1);
  }
}
