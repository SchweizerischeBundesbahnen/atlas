package ch.sbb.exportservice.job.prm.platform.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.platform.model.PlatformVersionCsvModel;
import ch.sbb.exportservice.job.prm.platform.model.PlatformVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvPlatformVersionWriter extends BaseCsvWriter<PlatformVersionCsvModel> {

  CsvPlatformVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.boardingDevice,
        Fields.adviceAccessInfo, Fields.additionalInformation, Fields.contrastingAreas, Fields.dynamicAudio,
        Fields.dynamicVisual, Fields.height, Fields.inclination, Fields.inclinationLongitudinal, Fields.inclinationWidth,
        Fields.infoOpportunities, Fields.levelAccessWheelchair, Fields.partialElevation, Fields.superElevation,
        Fields.tactileSystems, Fields.vehicleAccess, Fields.wheelChairAreaLength, Fields.wheelChairAreaWidth,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }
}
