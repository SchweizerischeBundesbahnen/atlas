package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
class SublineTemplateGenerator {

  static final SublineUpdateCsvModel SUBLINE_UPDATE_CSV_MODEL = SublineUpdateCsvModel.builder()
        .slnid("ch:1:slnid:1024328:1")
        .linienId("328:1")
      .validFrom(LocalDate.of(2021, 4, 1))
      .validTo(LocalDate.of(2099, 12, 31))
        .sublineConcessionType(SublineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
        .swissSublineNumber("b0.BEX:a")
        .description("Bern - Thun")
        .longName("Thun - Spiez")
        .businessOrganisation("ch:1:sboid:100001")
        .build();

}
