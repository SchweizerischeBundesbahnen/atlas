package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvTrafficPointElementVersionWriter extends BaseCsvWriter<TrafficPointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{Fields.sloid, Fields.numberShort, Fields.uicCountryCode,
        Fields.number, Fields.checkDigit, Fields.validFrom, Fields.validTo, Fields.designation,
        Fields.designationOperational, Fields.length, Fields.boardingAreaHeight, Fields.compassDirection, Fields.parentSloid,
        Fields.trafficPointElementType, Fields.lv95East, Fields.lv95North, Fields.wgs84East, Fields.wgs84North,
        Fields.height, Fields.creationDate, Fields.editionDate, Fields.parentSloidServicePoint, Fields.designationOfficial,
        Fields.servicePointBusinessOrganisation, Fields.servicePointBusinessOrganisationNumber,
        Fields.servicePointBusinessOrganisationAbbreviationDe, Fields.servicePointBusinessOrganisationAbbreviationFr,
        Fields.servicePointBusinessOrganisationAbbreviationIt, Fields.servicePointBusinessOrganisationAbbreviationEn,
        Fields.servicePointBusinessOrganisationDescriptionDe, Fields.servicePointBusinessOrganisationDescriptionFr,
        Fields.servicePointBusinessOrganisationDescriptionIt, Fields.servicePointBusinessOrganisationDescriptionEn
    };
  }

}
