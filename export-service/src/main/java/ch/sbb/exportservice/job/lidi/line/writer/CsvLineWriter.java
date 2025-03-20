package ch.sbb.exportservice.job.lidi.line.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.lidi.line.model.LineCsvModel;
import ch.sbb.exportservice.job.lidi.line.model.LineCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvLineWriter extends BaseCsvWriter<LineCsvModel> {

  CsvLineWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.slnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.lineType, Fields.concessionType,
        Fields.swissLineNumber, Fields.description, Fields.longName, Fields.number, Fields.shortNumber, Fields.offerCategory,
        Fields.businessOrganisation, Fields.comment, Fields.creationTime, Fields.editionTime
    };
  }

}
