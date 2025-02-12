package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.model.TimetableFieldNumberCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvTimetableFieldNumberWriter extends BaseCsvWriter<TimetableFieldNumberCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.ttfnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.swissTimetableFieldNumber, Fields.number,
        Fields.businessOrganisation, Fields.description, Fields.comment, Fields.lineRelations, Fields.creationTime,
        Fields.editionTime
    };
  }

}
