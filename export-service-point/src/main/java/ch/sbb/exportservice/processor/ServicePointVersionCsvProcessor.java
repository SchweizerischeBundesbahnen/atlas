package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationModel;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.ServicePointVersionCsvModelBuilder;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointVersionCsvProcessor extends BaseProcessor implements
    ItemProcessor<ServicePointVersion, ServicePointVersionCsvModel> {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH);
  private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  @Override
  public ServicePointVersionCsvModel process(ServicePointVersion version) {
    ServicePointVersionCsvModelBuilder builder = ServicePointVersionCsvModel.builder()
        .numberShort(version.getNumber().getNumberShort())
        .uicCountryCode(version.getCountry().getUicCode())
        .sloid(version.getSloid())
        .number(version.getNumber().getValue())
        .checkDigit(version.getNumber().getCheckDigit())
        .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER.format(version.getValidTo()))
        .designationOfficial(version.getDesignationOfficial())
        .designationLong(version.getDesignationLong())
        .abbreviation(version.getAbbreviation())
        .operatingPoint(version.isOperatingPoint())
        .operatingPointWithTimetable(version.isOperatingPointWithTimetable())
        .stopPoint(version.isStopPoint())
        .stopPointTypeCode(version.getStopPointType())
        .freightServicePoint(version.isFreightServicePoint())
        .trafficPoint(version.isTrafficPoint())
        .borderPoint(version.isBorderPoint())
        .hasGeolocation(version.hasGeolocation())
        .isoCoutryCode(version.getCountry().getIsoCode());
    if (version.getServicePointGeolocation() != null) {
      buildServicePointGeolocation(version, builder);
    }
    builder.operatingPointTypeCode(version.getOperatingPointType())
        .operatingPointTechnicalTimetableTypeCode(version.getOperatingPointTechnicalTimetableType())
        .meansOfTransportCode(version.getMeansOfTransportPipeList())
        .categoriesCode(version.getCategoriesPipeList())
        .operatingPointTrafficPointTypeCode(
            version.getOperatingPointTrafficPointType())
        .operatingPointRouteNetwork(version.isOperatingPointRouteNetwork())
        .operatingPointKilometer(version.isOperatingPointKilometer())
        .operatingPointKilometerMasterNumber(version.getOperatingPointKilometerMaster() != null ?
            version.getOperatingPointKilometerMaster().getValue() : null)
        .sortCodeOfDestinationStation(version.getSortCodeOfDestinationStation())
        .sboid(version.getBusinessOrganisation().getBusinessOrganisation())
        .businessOrganisationAbbreviationDe(version.getBusinessOrganisation().getBusinessOrganisationAbbreviationDe())
        .businessOrganisationAbbreviationFr(version.getBusinessOrganisation().getBusinessOrganisationAbbreviationFr())
        .businessOrganisationAbbreviationIt(version.getBusinessOrganisation().getBusinessOrganisationAbbreviationIt())
        .businessOrganisationAbbreviationEn(version.getBusinessOrganisation().getBusinessOrganisationAbbreviationEn())
        .businessOrganisationDescriptionDe(version.getBusinessOrganisation().getBusinessOrganisationDescriptionDe())
        .businessOrganisationDescriptionFr(version.getBusinessOrganisation().getBusinessOrganisationDescriptionFr())
        .businessOrganisationDescriptionIt(version.getBusinessOrganisation().getBusinessOrganisationDescriptionIt())
        .businessOrganisationDescriptionEn(version.getBusinessOrganisation().getBusinessOrganisationDescriptionEn())
        .fotComment(version.getComment())
        .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .statusDidok3(version.getStatusDidok3().name());
    return builder.build();
  }

  private void buildServicePointGeolocation(ServicePointVersion version, ServicePointVersionCsvModelBuilder builder) {
    builder.cantonAbbreviation(version.getServicePointGeolocation().getSwissCanton() != null ?
            version.getServicePointGeolocation().getSwissCanton().getAbbreviation() : null)
        .districtName(version.getServicePointGeolocation().getSwissDistrictName())
        .districtFsoName(version.getServicePointGeolocation().getSwissDistrictNumber())
        .municipalityName(version.getServicePointGeolocation().getSwissMunicipalityName())
        .fsoNumber(version.getServicePointGeolocation().getSwissMunicipalityNumber())
        .localityName(version.getServicePointGeolocation().getSwissLocalityName());
    ServicePointGeolocationModel geolocationModel = fromEntity(version.getServicePointGeolocation());
    builder.height(geolocationModel.getHeight())
        .lv95East(geolocationModel.getLv95().getEast())
        .lv95North(geolocationModel.getLv95().getNorth())
        .wgs84East(geolocationModel.getWgs84().getEast())
        .wgs84North(geolocationModel.getWgs84().getNorth())
        .wgs84WebEast(geolocationModel.getWgs84web().getEast())
        .wgs84WebNorth(geolocationModel.getWgs84web().getNorth());
  }

}
