package ch.sbb.exportservice.job.lidi.ttfn;

import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.lidi.ttfn.TimetableFieldNumberCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvTimetableFieldNumberWriter extends BaseCsvWriter<TimetableFieldNumberCsvModel> {

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.ttfnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.swissTimetableFieldNumber, Fields.number,
        Fields.businessOrganisation, Fields.description, Fields.comment, Fields.lineRelations, Fields.creationTime,
        Fields.editionTime
    };
  }

}
