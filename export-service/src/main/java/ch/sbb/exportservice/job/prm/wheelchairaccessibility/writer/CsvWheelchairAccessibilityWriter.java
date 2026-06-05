package ch.sbb.exportservice.job.prm.wheelchairaccessibility.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.model.WheelchairAccessibilityCsvModel;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.model.WheelchairAccessibilityCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvWheelchairAccessibilityWriter extends BaseCsvWriter<WheelchairAccessibilityCsvModel> {

  CsvWheelchairAccessibilityWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.number, Fields.sloid, Fields.type, Fields.accessibility,
        Fields.validFrom, Fields.validTo
    };
  }

}
