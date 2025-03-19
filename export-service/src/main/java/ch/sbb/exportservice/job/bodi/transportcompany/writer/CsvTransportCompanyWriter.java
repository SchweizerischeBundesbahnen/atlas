package ch.sbb.exportservice.job.bodi.transportcompany.writer;

import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.bodi.transportcompany.model.TransportCompanyCsvModel;
import ch.sbb.exportservice.job.bodi.transportcompany.model.TransportCompanyCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvTransportCompanyWriter extends BaseCsvWriter<TransportCompanyCsvModel> {

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.id, Fields.number, Fields.abbreviation, Fields.description, Fields.businessRegisterName,
        Fields.transportCompanyStatus, Fields.businessRegisterNumber, Fields.enterpriseId, Fields.ricsCode,
        Fields.businessOrganisationNumbers, Fields.comment, Fields.creationDate, Fields.editionDate
    };
  }

}
