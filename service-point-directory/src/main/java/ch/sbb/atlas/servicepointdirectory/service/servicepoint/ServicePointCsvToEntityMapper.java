package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointStatus;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServicePointCsvToEntityMapper implements
    Function<ServicePointCsvModel, ServicePointVersion> {

  private static Set<Category> getCategories(ServicePointCsvModel servicePointCsvModel) {
    return Arrays.stream(Objects.nonNull(servicePointCsvModel.getDsKategorienIds())
            ? servicePointCsvModel.getDsKategorienIds().split("\\|") :
            new String[]{})
        .map(categoryIdStr -> Category.from(Integer.parseInt(categoryIdStr)))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  public ServicePointVersion apply(ServicePointCsvModel servicePointCsvModel) {
    ServicePointVersion servicePointVersion = mapServicePointVersion(servicePointCsvModel);
    ServicePointGeolocation servicePointGeolocation = mapGeolocation(servicePointCsvModel);
    GeolocationMapper.transformLv03andWgs84(servicePointGeolocation);

    if (servicePointGeolocation.isValid()) {
      servicePointVersion.setServicePointGeolocation(servicePointGeolocation);
      servicePointGeolocation.setServicePointVersion(servicePointVersion);
    }

    return servicePointVersion;
  }

  ServicePointGeolocation mapGeolocation(
      ServicePointCsvModel servicePointCsvModel) {
    return ServicePointGeolocation
        .builder()
        .spatialReference(servicePointCsvModel.getSpatialReference())
        .east(servicePointCsvModel.getOriginalEast())
        .north(servicePointCsvModel.getOriginalNorth())
        .height(servicePointCsvModel.getHeight())
        .country(Country.fromIsoCode(servicePointCsvModel.getIsoCountryCode()))
        .swissCanton(SwissCanton.fromCantonNumber(servicePointCsvModel.getKantonsNum()))
        .swissDistrictName(servicePointCsvModel.getBezirksName())
        .swissDistrictNumber(servicePointCsvModel.getBezirksNum())
        .swissMunicipalityNumber(servicePointCsvModel.getBfsNummer())
        .swissMunicipalityName(servicePointCsvModel.getGemeindeName())
        .swissLocalityName(servicePointCsvModel.getOrtschaftsName())
        .creationDate(servicePointCsvModel.getCreatedAt())
        .creator(servicePointCsvModel.getCreatedBy())
        .editionDate(servicePointCsvModel.getEditedAt())
        .editor(servicePointCsvModel.getEditedBy())
        .build();
  }

  ServicePointVersion mapServicePointVersion(ServicePointCsvModel servicePointCsvModel) {
    Set<MeanOfTransport> meansOfTransport = MeanOfTransport.fromCode(servicePointCsvModel.getBpvhVerkehrsmittel());
    ServicePointStatus statusDidok3 = ServicePointStatus.from(servicePointCsvModel.getStatus());
    Status status = calculateStatus(statusDidok3);
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(servicePointCsvModel.getDidokCode()))
        .sloid(servicePointCsvModel.getSloid())
        .numberShort(servicePointCsvModel.getNummer())
        .country(Country.from(servicePointCsvModel.getLaendercode()))
        .designationLong(servicePointCsvModel.getBezeichnungLang())
        .designationOfficial(servicePointCsvModel.getBezeichnungOffiziell())
        .abbreviation(servicePointCsvModel.getAbkuerzung())
        .businessOrganisation("ch:1:sboid:" + servicePointCsvModel.getSaid())
        .status(status)
        .validFrom(servicePointCsvModel.getValidFrom())
        .validTo(servicePointCsvModel.getValidTo())
        .categories(getCategories(servicePointCsvModel))
        .meansOfTransport(meansOfTransport)
        .stopPointType(StopPointType.from(servicePointCsvModel.getHTypId()))
        .operatingPointType(OperatingPointType.from(servicePointCsvModel.getBpBetriebspunktArtId()))
        .operatingPointTechnicalTimetableType(
            OperatingPointTechnicalTimetableType.from(servicePointCsvModel.getBptfBetriebspunktArtId()))
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.from(servicePointCsvModel.getBpvbBetriebspunktArtId()))
        .freightServicePoint(servicePointCsvModel.getIsBedienpunkt())
        .operatingPoint(servicePointCsvModel.getIsBetriebspunkt())
        .operatingPointWithTimetable(servicePointCsvModel.getIsFahrplan())
        .sortCodeOfDestinationStation(servicePointCsvModel.getRichtpunktCode())
        .operatingPointRouteNetwork(
            Boolean.TRUE.equals(servicePointCsvModel.getOperatingPointRouteNetwork()))
        .operatingPointKilometerMaster(
            Optional.ofNullable(servicePointCsvModel.getOperatingPointKilometerMaster())
                .map(ServicePointNumber::ofNumberWithoutCheckDigit)
                .orElse(null))
        .creationDate(servicePointCsvModel.getCreatedAt())
        .creator(servicePointCsvModel.getCreatedBy())
        .editionDate(servicePointCsvModel.getEditedAt())
        .editor(servicePointCsvModel.getEditedBy())
        .build();
  }

  private Status calculateStatus(ServicePointStatus servicePointStatus) {
    return switch (servicePointStatus) {
      case TO_BE_REQUESTED -> Status.DRAFT;
      case REQUESTED -> Status.IN_REVIEW;
      default -> Status.VALIDATED;
    };
  }

}
