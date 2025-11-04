package ch.sbb.exportservice.job.lidi.ttfn.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.lidi.ttfn.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.job.lidi.ttfn.model.TimetableFieldNumberCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvTimetableFieldNumberWriter extends BaseCsvWriter<TimetableFieldNumberCsvModel> {

  CsvTimetableFieldNumberWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.ttfnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.swissTimetableFieldNumber, Fields.number,
        Fields.businessOrganisation, Fields.descriptionOutwardLine1, Fields.descriptionOutwardLine2,
        Fields.descriptionOutwardLine3, Fields.descriptionReturnLine1, Fields.descriptionReturnLine2,
        Fields.descriptionReturnLine3, Fields.meanOfTransport, Fields.lineRelations, Fields.creationTime, Fields.editionTime
    };
  }

}
