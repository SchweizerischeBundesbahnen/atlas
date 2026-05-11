package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SublineTemplateGenerator {

  private static final LocalDate VALID_FROM = LocalDate.of(2021, 4, 1);
  private static final LocalDate VALID_TO = LocalDate.of(2099, 12, 31);

  public static SublineUpdateCsvModel getUpdateExample() {
    return SublineUpdateCsvModel.builder()
        .slnid("ch:1:slnid:1024320:1")
        .linienId("320")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
        .longName("Bernina Express")
        .businessOrganisation("ch:1:sboid:100053")
        .build();
  }

}
