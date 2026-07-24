package ch.sbb.line.directory.module.bulkimport.line.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LineBulkImportCreateTest {


  @Test
  void shouldMapFromCsvToCreateModel() {
    BulkImportUpdateContainer<LineCreateCsvModel> container =
        BulkImportUpdateContainer.<LineCreateCsvModel>builder()
            .object(LineCreateCsvModel.builder()
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
                .build())
            .build();

    LineVersionModelV2 expected = LineVersionModelV2.builder()
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

    LineVersionModelV2 result = LineBulkImportCreate.apply(container);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

}
