package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.LoadingPointVersionCsvModel.Fields;
import ch.sbb.exportservice.model.StopPointVersionCsvModel;
import org.springframework.stereotype.Component;

@Component
public class CsvStopPointVersionWriter extends BaseCsvWriter<StopPointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.number, Fields.designation, Fields.validFrom, Fields.validTo, Fields.checkDigit, Fields.parentSloidServicePoint, Fields.creationDate, Fields.editionDate,
//        Fields.servicePointBusinessOrganisation,
//        Fields.servicePointBusinessOrganisationNumber,
//        Fields.servicePointBusinessOrganisationAbbreviationDe, Fields.servicePointBusinessOrganisationAbbreviationFr,
//        Fields.servicePointBusinessOrganisationAbbreviationIt, Fields.servicePointBusinessOrganisationAbbreviationEn,
//        Fields.servicePointBusinessOrganisationDescriptionDe, Fields.servicePointBusinessOrganisationDescriptionFr,
//        Fields.servicePointBusinessOrganisationDescriptionIt, Fields.servicePointBusinessOrganisationDescriptionEn
    };
  }

}
