package ch.sbb.line.directory.module.ttfn.service.quovadis;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisDataMapper.TimetableFieldNumberV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
class QuoVadisToAtlasMapper {

  static TimetableFieldNumberVersion toEntity(TimetableFieldNumberV2 fieldNumber) {
    return TimetableFieldNumberVersion.builder()
//        .swissTimetableFieldNumber()  Was machen wir hier mit dem? Die bekommen wir von Quovadis nicht ....
//        .businessOrganisation()  Was machen wir hier mit dem? Die bekommen wir von Quovadis nicht ....
        .number(fieldNumber.getNumber())
        .description(fieldNumber.getDescriptionOutwardLine1())
        .validFrom(QuoVadisDataImportService.FIRST_DAY_OF_FP_2026)
        .validTo(LocalDate.of(9999, 12, 31))
        .status(Status.VALIDATED)
        .build();
  }
}
