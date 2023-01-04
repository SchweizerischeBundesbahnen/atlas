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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

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
        .lv03east(servicePointCsvModel.getELV03())
        .lv03north(servicePointCsvModel.getNLV03())
        .lv95east(servicePointCsvModel.getELV95())
        .lv95north(servicePointCsvModel.getNLV95())
        .wgs84east(servicePointCsvModel.getEWGS84())
        .wgs84north(servicePointCsvModel.getNWGS84())
        .wgs84webEast(servicePointCsvModel.getEWGS84WEB())
        .wgs84webNorth(servicePointCsvModel.getNWGS84WEB())
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
    Set<MeanOfTransport> meansOfTransport = Arrays.stream(Objects.nonNull(servicePointCsvModel.getBpvhVerkehrsmittel())
            ? servicePointCsvModel.getBpvhVerkehrsmittel().split("~") :
            new String[]{})
        .map(MeanOfTransport::from)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
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
        .categories(
            Arrays.stream(Objects.nonNull(servicePointCsvModel.getDsKategorienIds())
                    ? servicePointCsvModel.getDsKategorienIds().split("\\|") :
                    new String[]{})
                .map(categoryIdStr -> Category.from(Integer.parseInt(categoryIdStr)))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
        )
        .meansOfTransport(            meansOfTransport        )
        .stopPointType(StopPointType.from(servicePointCsvModel.getHTypId()))
        .operatingPointType(
            meansOfTransport.isEmpty() ?
            OperatingPointType.from(ObjectUtils.firstNonNull(servicePointCsvModel.getBpvbBetriebspunktArtId(),
                servicePointCsvModel.getBpofBetriebspunktArtId(), servicePointCsvModel.getBptfBetriebspunktArtId())):
            OperatingPointType.STOP_POINT)
        .creationDate(servicePointCsvModel.getCreatedAt())
        .creator(servicePointCsvModel.getCreatedBy())
        .editionDate(servicePointCsvModel.getEditedAt())
        .editor(servicePointCsvModel.getEditedBy())
        .comment(servicePointCsvModel.getComment())
        .build();
  }

}
