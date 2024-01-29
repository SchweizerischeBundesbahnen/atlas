package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvReferencePointVersionWriter extends BaseCsvWriter<ReferencePointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.mainReferencePoint, Fields.additionalInformation, Fields.rpType,
        Fields.validFrom, Fields.validTo, Fields.creationDate, Fields.creator, Fields.editionDate, Fields.editor
    };
  }

}
