package ch.sbb.exportservice.job.sepodi.servicepoint.processor;

import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.job.sepodi.servicepoint.model.ServicePointVersionCsvModel.ServicePointVersionCsvModelBuilder;
import ch.sbb.exportservice.util.MapperUtil;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointVersionCsvProcessor extends BaseSepodiProcessor implements
    ItemProcessor<ServicePointVersion, ServicePointVersionCsvModel> {

  @Override
  public ServicePointVersionCsvModel process(ServicePointVersion version) {
    ServicePointVersionCsvModelBuilder builder = ServicePointVersionCsvModel.builder()
        .numberShort(version.getNumber().getNumberShort())
        .uicCountryCode(version.getCountry().getUicCode())
        .sloid(version.getSloid())
        .number(version.getNumber().getNumber())
        .checkDigit(version.getNumber().getCheckDigit())
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
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
        .businessOrganisation(version.getSharedBusinessOrganisation().getBusinessOrganisation())
        .businessOrganisationNumber(version.getSharedBusinessOrganisation().getBusinessOrganisationNumber())
        .businessOrganisationAbbreviationDe(version.getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationDe())
        .businessOrganisationAbbreviationFr(version.getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationFr())
        .businessOrganisationAbbreviationIt(version.getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationIt())
        .businessOrganisationAbbreviationEn(version.getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationEn())
        .businessOrganisationDescriptionDe(version.getSharedBusinessOrganisation().getBusinessOrganisationDescriptionDe())
        .businessOrganisationDescriptionFr(version.getSharedBusinessOrganisation().getBusinessOrganisationDescriptionFr())
        .businessOrganisationDescriptionIt(version.getSharedBusinessOrganisation().getBusinessOrganisationDescriptionIt())
        .businessOrganisationDescriptionEn(version.getSharedBusinessOrganisation().getBusinessOrganisationDescriptionEn())
        .fotComment(version.getComment())
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
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
