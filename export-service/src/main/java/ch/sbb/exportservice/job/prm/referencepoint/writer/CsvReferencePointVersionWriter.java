package ch.sbb.exportservice.job.prm.referencepoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.referencepoint.model.ReferencePointVersionCsvModel;
import ch.sbb.exportservice.job.prm.referencepoint.model.ReferencePointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvReferencePointVersionWriter extends BaseCsvWriter<ReferencePointVersionCsvModel> {

  CsvReferencePointVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.mainReferencePoint, Fields.additionalInformation, Fields.referencePointType,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
