package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.BusinessOrganisationCsvModel;
import ch.sbb.exportservice.model.BusinessOrganisationCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvBusinessOrganisationWriter extends BaseCsvWriter<BusinessOrganisationCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sboid, Fields.said, Fields.validFrom, Fields.validTo, Fields.organisationNumber, Fields.status,
        Fields.descriptionDe, Fields.descriptionFr, Fields.descriptionIt, Fields.descriptionEn, Fields.abbreviationDe,
        Fields.abbreviationFr, Fields.abbreviationIt, Fields.abbreviationEn, Fields.businessTypesId, Fields.businessTypesDe,
        Fields.businessTypesIt, Fields.businessTypesFr, Fields.transportCompanyNumber, Fields.transportCompanyAbbreviation,
        Fields.transportCompanyBusinessRegisterName, Fields.creationTime, Fields.editionTime
    };
  }

}
