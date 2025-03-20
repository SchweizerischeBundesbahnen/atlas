package ch.sbb.exportservice.job.bodi.businessorganisation.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisationCsvModel;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisationCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvBusinessOrganisationWriter extends BaseCsvWriter<BusinessOrganisationCsvModel> {

  CsvBusinessOrganisationWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sboid, Fields.said, Fields.validFrom, Fields.validTo, Fields.organisationNumber, Fields.status,
        Fields.descriptionDe, Fields.descriptionFr, Fields.descriptionIt, Fields.descriptionEn, Fields.abbreviationDe,
        Fields.abbreviationFr, Fields.abbreviationIt, Fields.abbreviationEn, Fields.businessTypesId, Fields.businessTypesDe,
        Fields.businessTypesIt, Fields.businessTypesFr, Fields.transportCompanyNumber, Fields.transportCompanyAbbreviation,
        Fields.transportCompanyBusinessRegisterName, Fields.creationTime, Fields.editionTime
    };
  }

}
