package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.LineCsvModel;
import ch.sbb.exportservice.model.LineCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvLineWriter extends BaseCsvWriter<LineCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.slnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.lineType, Fields.concessionType,
        Fields.swissLineNumber, Fields.description, Fields.longName, Fields.number, Fields.shortNumber, Fields.offerCategory,
        Fields.businessOrganisation, Fields.comment, Fields.creationTime, Fields.editionTime
    };
  }

}
