package ch.sbb.exportservice.job.lidi.subline.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.lidi.subline.model.SublineCsvModel;
import ch.sbb.exportservice.job.lidi.subline.model.SublineCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvSublineWriter extends BaseCsvWriter<SublineCsvModel> {

  CsvSublineWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.slnid, Fields.mainlineSlnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.sublineType,
        Fields.concessionType, Fields.swissSublineNumber, Fields.swissLineNumber, Fields.number, Fields.shortNumber,
        Fields.offerCategory,
        Fields.description, Fields.longName, Fields.businessOrganisation, Fields.creationTime, Fields.editionTime
    };
  }

}
