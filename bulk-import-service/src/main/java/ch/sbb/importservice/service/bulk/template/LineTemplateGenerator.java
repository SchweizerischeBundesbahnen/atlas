package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineTemplateGenerator {

  public static LineUpdateCsvModel getUpdateExample() {
    return LineUpdateCsvModel.builder()
        .slnid("ch:1:slnid:1024320")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .number("BEX")
        .swissLineNumber("b0.BEX")
        .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .offerCategory(OfferCategory.IR)
        .shortNumber("EX")
        .longName("Bernina Express")
        .businessOrganisation("ch:1:sboid:100053")
        .comment("Bernina Express / Konzessionsrecht ist nur für den schweizerischen Linienabschnitt gültig")
        .build();
  }

}
