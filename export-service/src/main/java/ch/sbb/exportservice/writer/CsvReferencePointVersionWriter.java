package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvReferencePointVersionWriter extends BaseCsvWriter<ReferencePointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.mainReferencePoint, Fields.additionalInformation, Fields.referencePointType,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
