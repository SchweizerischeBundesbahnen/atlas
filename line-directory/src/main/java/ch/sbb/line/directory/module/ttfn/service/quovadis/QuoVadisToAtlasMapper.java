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
        .number(fieldNumber.getNumber())
        .descriptionOutwardLine1(fieldNumber.getDescriptionOutwardLine1())
        .descriptionOutwardLine2(fieldNumber.getDescriptionOutwardLine2())
        .descriptionOutwardLine3(fieldNumber.getDescriptionOutwardLine3())
        .descriptionReturnLine1(fieldNumber.getDescriptionReturnLine1())
        .descriptionReturnLine2(fieldNumber.getDescriptionReturnLine2())
        .descriptionReturnLine3(fieldNumber.getDescriptionReturnLine3())
        .meanOfTransport(fieldNumber.getMeanOfTransport())
        .validFrom(QuoVadisDataImportService.FIRST_DAY_OF_FP_2026)
        .validTo(LocalDate.of(9999, 12, 31))
        .status(Status.VALIDATED)
        .build();
  }
}
