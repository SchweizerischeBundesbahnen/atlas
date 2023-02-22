package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointWithoutTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
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

  private static Set<MeanOfTransport> getMeansOfTransport(
      ServicePointCsvModel servicePointCsvModel) {
    return Arrays.stream(Objects.nonNull(servicePointCsvModel.getBpvhVerkehrsmittel())
            ? servicePointCsvModel.getBpvhVerkehrsmittel().split("~") :
            new String[]{})
        .map(MeanOfTransport::from)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  public ServicePointVersion apply(ServicePointCsvModel servicePointCsvModel) {
    ServicePointVersion servicePointVersion = mapServicePointVersion(servicePointCsvModel);
    ServicePointGeolocation servicePointGeolocation = mapGeolocation(servicePointCsvModel);

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
        .country(Country.from(servicePointCsvModel.getLaendercode()))
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
    Set<MeanOfTransport> meansOfTransport = getMeansOfTransport(servicePointCsvModel);
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(servicePointCsvModel.getDidokCode()))
        .sloid(servicePointCsvModel.getSloid())
        .numberShort(servicePointCsvModel.getNummer())
        .country(Country.from(servicePointCsvModel.getLaendercode()))
        .designationLong(servicePointCsvModel.getBezeichnungLang())
        .designationOfficial(servicePointCsvModel.getBezeichnungOffiziell())
        .abbreviation(servicePointCsvModel.getAbkuerzung())
        .statusDidok3(
            ServicePointStatus.from(servicePointCsvModel.getStatus()))
        .businessOrganisation("ch:1:sboid:" + servicePointCsvModel.getSaid())
        .status(Status.VALIDATED)
        .validFrom(servicePointCsvModel.getValidFrom())
        .validTo(servicePointCsvModel.getValidTo())
        .categories(getCategories(servicePointCsvModel))
        .meansOfTransport(meansOfTransport)
        .stopPointType(StopPointType.from(servicePointCsvModel.getHTypId()))
        .operatingPointType(OperatingPointType.from(servicePointCsvModel.getBpBetriebspunktArtId()))
        .operatingPointWithoutTimetableType(OperatingPointWithoutTimetableType.from(servicePointCsvModel.getBpofBetriebspunktArtId()))
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.from(servicePointCsvModel.getBptfBetriebspunktArtId()))
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.from(servicePointCsvModel.getBpvbBetriebspunktArtId()))
        .freightServicePoint(servicePointCsvModel.getIsBedienpunkt())
        .operatingPoint(servicePointCsvModel.getIsBetriebspunkt())
        .operatingPointWithTimetable(servicePointCsvModel.getIsFahrplan())
        .sortCodeOfDestinationStation(servicePointCsvModel.getRichtpunktCode())
        .operatingPointRouteNetwork(
            Boolean.TRUE.equals(servicePointCsvModel.getOperatingPointRouteNetwork()))
        .operatingPointKilometerMaster(
            Optional.ofNullable(servicePointCsvModel.getOperatingPointKilometerMaster())
                .map(ServicePointNumber::of)
                .orElse(null))
        .creationDate(servicePointCsvModel.getCreatedAt())
        .creator(servicePointCsvModel.getCreatedBy())
        .editionDate(servicePointCsvModel.getEditedAt())
        .editor(servicePointCsvModel.getEditedBy())
        .comment(servicePointCsvModel.getComment())
        .build();
  }

}
