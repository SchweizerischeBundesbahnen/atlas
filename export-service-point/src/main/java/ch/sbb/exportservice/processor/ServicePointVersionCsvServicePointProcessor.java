package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.ServicePointVersionCsvModelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
public class ServicePointVersionCsvServicePointProcessor extends BaseServicePointProcessor implements
    ItemProcessor<ServicePointVersion, ServicePointVersionCsvModel> {

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
        .stopPointType(version.getStopPointType())
        .freightServicePoint(version.isFreightServicePoint())
        .trafficPoint(version.isTrafficPoint())
        .borderPoint(version.isBorderPoint())
        .hasGeolocation(version.hasGeolocation());
    if (version.getServicePointGeolocation() != null) {
      buildServicePointGeolocation(version, builder);
    }
    builder.operatingPointType(version.getOperatingPointType())
        .operatingPointTechnicalTimetableType(version.getOperatingPointTechnicalTimetableType())
        .meansOfTransport(version.getMeansOfTransportPipeList())
        .categories(version.getCategoriesPipeList())
        .operatingPointTrafficPointType(
            version.getOperatingPointTrafficPointType())
        .operatingPointRouteNetwork(version.isOperatingPointRouteNetwork())
        .operatingPointKilometer(version.isOperatingPointKilometer())
        .operatingPointKilometerMasterNumber(version.getOperatingPointKilometerMaster() != null ?
            version.getOperatingPointKilometerMaster().getValue() : null)
        .sortCodeOfDestinationStation(version.getSortCodeOfDestinationStation())
        .businessOrganisation(version.getBusinessOrganisation().getBusinessOrganisation())
        .businessOrganisationNumber(version.getBusinessOrganisation().getBusinessOrganisationNumber())
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
        .status(version.getStatus());
    return builder.build();
  }

  private void buildServicePointGeolocation(ServicePointVersion version, ServicePointVersionCsvModelBuilder builder) {
    if (version.getServicePointGeolocation().getCountry() != null) {
      builder.isoCountryCode(version.getServicePointGeolocation().getCountry().getIsoCode());
    }

    Optional<SwissCanton> swissCanton = Optional.ofNullable(version.getServicePointGeolocation().getSwissCanton());
    swissCanton.ifPresent(canton -> builder.cantonAbbreviation(canton.getAbbreviation()).cantonName(canton.getName())
        .cantonFsoNumber(canton.getNumber()));

    builder.districtName(version.getServicePointGeolocation().getSwissDistrictName())
        .districtFsoNumber(version.getServicePointGeolocation().getSwissDistrictNumber())
        .municipalityName(version.getServicePointGeolocation().getSwissMunicipalityName())
        .fsoNumber(version.getServicePointGeolocation().getSwissMunicipalityNumber())
        .localityName(version.getServicePointGeolocation().getSwissLocalityName());
    ServicePointGeolocationReadModel geolocationModel = fromEntity(version.getServicePointGeolocation());
    builder.height(geolocationModel.getHeight())
        .lv95East(geolocationModel.getLv95().getEast())
        .lv95North(geolocationModel.getLv95().getNorth())
        .wgs84East(geolocationModel.getWgs84().getEast())
        .wgs84North(geolocationModel.getWgs84().getNorth());
  }

}
