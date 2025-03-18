package ch.sbb.exportservice.job.referencepoint;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel.Fields;
import ch.sbb.exportservice.job.BaseCsvWriter;
import org.springframework.stereotype.Component;

@Component
public class CsvReferencePointVersionWriter extends BaseCsvWriter<ReferencePointVersionCsvModel> {

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
