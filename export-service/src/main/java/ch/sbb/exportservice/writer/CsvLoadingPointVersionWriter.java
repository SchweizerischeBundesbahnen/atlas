package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvLoadingPointVersionWriter extends BaseCsvWriter<LoadingPointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.number, Fields.designation, Fields.designationLong, Fields.connectionPoint, Fields.validFrom, Fields.validTo,
        Fields.servicePointNumber, Fields.checkDigit, Fields.parentSloidServicePoint, Fields.creationDate, Fields.editionDate,
        Fields.servicePointBusinessOrganisation, Fields.servicePointBusinessOrganisationNumber,
        Fields.servicePointBusinessOrganisationAbbreviationDe, Fields.servicePointBusinessOrganisationAbbreviationFr,
        Fields.servicePointBusinessOrganisationAbbreviationIt, Fields.servicePointBusinessOrganisationAbbreviationEn,
        Fields.servicePointBusinessOrganisationDescriptionDe, Fields.servicePointBusinessOrganisationDescriptionFr,
        Fields.servicePointBusinessOrganisationDescriptionIt, Fields.servicePointBusinessOrganisationDescriptionEn
    };
  }

}
