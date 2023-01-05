package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.service.util.GeolocationMapperUtil;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class ServicePointCsvToEntityMapper implements
    Function<ServicePointCsvModel, ServicePointVersion> {

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
        .east(GeolocationMapperUtil.getOriginalEast(
            servicePointCsvModel.getSpatialReference(),
            servicePointCsvModel.getEWGS84(),
            servicePointCsvModel.getEWGS84WEB(),
            servicePointCsvModel.getELV95(),
            servicePointCsvModel.getELV03()
        ))
        .north(GeolocationMapperUtil.getOriginalNorth(
            servicePointCsvModel.getSpatialReference(),
            servicePointCsvModel.getNWGS84(),
            servicePointCsvModel.getNWGS84WEB(),
            servicePointCsvModel.getNLV95(),
            servicePointCsvModel.getNLV03()
        ))
        .height(servicePointCsvModel.getHeight())
        .country(Country.from(servicePointCsvModel.getLaendercode()))
        .swissCantonFsoNumber(servicePointCsvModel.getBfsNummer())
        .swissCantonName(servicePointCsvModel.getKantonsName())
        .swissCantonNumber(servicePointCsvModel.getKantonsNum())
        .swissDistrictName(servicePointCsvModel.getBezirksName())
        .swissDistrictNumber(servicePointCsvModel.getBezirksNum())
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
        .validFrom(LocalDate.parse(servicePointCsvModel.getGueltigVon()))
        .validTo(LocalDate.parse(servicePointCsvModel.getGueltigBis()))
        .categories(getCategories(servicePointCsvModel))
        .meansOfTransport(meansOfTransport)
        .stopPointType(StopPointType.from(servicePointCsvModel.getHTypId()))
        .operatingPointType(getOperatingPointType(servicePointCsvModel, meansOfTransport))
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

  private static OperatingPointType getOperatingPointType(ServicePointCsvModel servicePointCsvModel,
      Set<MeanOfTransport> meansOfTransport) {
    if (!meansOfTransport.isEmpty()) {
      return OperatingPointType.STOP_POINT;
    }
    if (StringUtils.isNotBlank(servicePointCsvModel.getRichtpunktCode())) {
      return OperatingPointType.FREIGHT_POINT;
    }
    return OperatingPointType.from(
        ObjectUtils.firstNonNull(servicePointCsvModel.getBpBetriebspunktArtId(),
            servicePointCsvModel.getBpvbBetriebspunktArtId(),
            servicePointCsvModel.getBpofBetriebspunktArtId(),
            servicePointCsvModel.getBptfBetriebspunktArtId()));
  }

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

}
