package ch.sbb.exportservice.writer;

import static ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields.numberShort;

import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvServicePointVersionWriter extends BaseCsvWriter<ServicePointVersionCsvModel>{

  @Override
  String[] getCsvHeader() {
    return  new String[]{numberShort, Fields.uicCountryCode,
            Fields.sloid, Fields.number, Fields.checkDigit, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
            Fields.designationLong, Fields.abbreviation, Fields.operatingPoint, Fields.operatingPointWithTimetable, Fields.stopPoint,
            Fields.stopPointType, Fields.freightServicePoint, Fields.trafficPoint,
            Fields.borderPoint, Fields.hasGeolocation, Fields.isoCountryCode,
            Fields.cantonName, Fields.cantonFsoNumber, Fields.cantonAbbreviation,
            Fields.districtName, Fields.districtFsoNumber, Fields.municipalityName, Fields.fsoNumber,
            Fields.localityName, Fields.operatingPointType, Fields.operatingPointTechnicalTimetableType,
            Fields.meansOfTransport, Fields.categories, Fields.operatingPointTrafficPointType,
            Fields.operatingPointRouteNetwork, Fields.operatingPointKilometer, Fields.operatingPointKilometerMasterNumber,
            Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.businessOrganisationNumber,
            Fields.businessOrganisationAbbreviationDe, Fields.businessOrganisationAbbreviationFr,
            Fields.businessOrganisationAbbreviationIt, Fields.businessOrganisationAbbreviationEn,
            Fields.businessOrganisationDescriptionDe, Fields.businessOrganisationDescriptionFr,
            Fields.businessOrganisationDescriptionIt, Fields.businessOrganisationDescriptionEn, Fields.fotComment, Fields.lv95East,
            Fields.lv95North, Fields.wgs84East, Fields.wgs84North,
            Fields.height, Fields.creationDate, Fields.editionDate, Fields.status
    };
  }

}
