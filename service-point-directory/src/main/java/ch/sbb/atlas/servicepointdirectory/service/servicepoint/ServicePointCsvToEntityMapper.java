package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServicePointCsvToEntityMapper implements
    Function<ServicePointCsvModel, ServicePointVersion> {

  @Override
  public ServicePointVersion apply(ServicePointCsvModel servicePointCsvModel) {
    ServicePointVersion servicePointVersion = mapSPFromServicePointCsvModel(servicePointCsvModel);
    ServicePointGeolocation servicePointGeolocation = mapSPGeolocationFromServicePointCsvModel(
        servicePointCsvModel);

    if (servicePointGeolocation.isValid()) {
      servicePointVersion.setServicePointGeolocation(servicePointGeolocation);
      servicePointGeolocation.setServicePointVersion(servicePointVersion);
    }

    return servicePointVersion;
  }

  ServicePointGeolocation mapSPGeolocationFromServicePointCsvModel(
      ServicePointCsvModel servicePointCsvModel) {
    return ServicePointGeolocation
        .builder()
        .locationTypes(LocationTypes
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
            .build())
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

  ServicePointVersion mapSPFromServicePointCsvModel(ServicePointCsvModel servicePointCsvModel) {
    return ServicePointVersion
        .builder()
        .number(servicePointCsvModel.getDidokCode())
        .sloid(servicePointCsvModel.getSloid())
        .checkDigit(servicePointCsvModel.getDidokCode() % 10)
        .numberShort(servicePointCsvModel.getNummer())
        .country(Country.from(servicePointCsvModel.getLaendercode()))
        .designationLong(servicePointCsvModel.getBezeichnungLang())
        .designationOfficial(servicePointCsvModel.getBezeichnungOffiziell())
        .abbreviation(servicePointCsvModel.getAbkuerzung())
        .statusDidok3(
            ServicePointStatus.from(servicePointCsvModel.getStatus()))
        .businessOrganisation("ch:1:sboid:"
            + servicePointCsvModel.getSaid()) // TODO: check if this is
        // correct
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
        .meansOfTransport(
            Arrays.stream(Objects.nonNull(servicePointCsvModel.getBpvhVerkehrsmittel())
                      ? servicePointCsvModel.getBpvhVerkehrsmittel().split("~") :
                      new String[]{})
                  .map(MeanOfTransport::from)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toSet())
        )
        .stopPlaceType(StopPlaceType.from(servicePointCsvModel.getHTypId()))
        .operatingPointType(
            OperatingPointType.from(servicePointCsvModel.getBpvbBetriebspunktArtId()))
        .creationDate(servicePointCsvModel.getCreatedAt())
        .creator(servicePointCsvModel.getCreatedBy())
        .editionDate(servicePointCsvModel.getEditedAt())
        .editor(servicePointCsvModel.getEditedBy())
        .comment(servicePointCsvModel.getComment())
        .build();
  }

}
