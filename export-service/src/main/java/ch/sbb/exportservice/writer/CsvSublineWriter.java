package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.SublineCsvModel;
import ch.sbb.exportservice.model.SublineCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvSublineWriter extends BaseCsvWriter<SublineCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.slnid, Fields.mainlineSlnid, Fields.validFrom, Fields.validTo, Fields.status, Fields.sublineType,
        Fields.concessionType, Fields.swissSublineNumber, Fields.number, Fields.shortNumber, Fields.offerCategory,
        Fields.description, Fields.longName, Fields.businessOrganisation, Fields.creationTime, Fields.editionTime
    };
  }

}
