package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrafficPointElementCsvToEntityMapper implements Function<TrafficPointElementCsvModel, TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion apply(TrafficPointElementCsvModel trafficPointElementCsvModel) {
    TrafficPointElementGeolocation geolocation = null;
    // TODO stam: since 17.12.2023 new flag in CSV - use // if (!trafficPointElementCsvModel.getIS_VIRTUELL()) {
    if (trafficPointElementCsvModel.getEWgs84() != null) {
      geolocation = TrafficPointElementGeolocation.builder()
        .country(Country.from(trafficPointElementCsvModel.getCountry()))
        .locationTypes(LocationTypes.builder()
          .spatialReference(trafficPointElementCsvModel.getSpatialReference())
          .lv03east(trafficPointElementCsvModel.getELv03())
          .lv03north(trafficPointElementCsvModel.getNLv03())
          .lv95east(trafficPointElementCsvModel.getELv95())
          .lv95north(trafficPointElementCsvModel.getNLv95())
          .wgs84east(trafficPointElementCsvModel.getEWgs84())
          .wgs84north(trafficPointElementCsvModel.getNWgs84())
          .wgs84webEast(trafficPointElementCsvModel.getEWgs84web())
          .wgs84webNorth(trafficPointElementCsvModel.getNWgs84web())
          .height(trafficPointElementCsvModel.getHeight())
        .build())
        .creator(trafficPointElementCsvModel.getCreatedBy())
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(trafficPointElementCsvModel.getEditedBy())
        .editionDate(trafficPointElementCsvModel.getEditedAt())
        .build();

      if (!geolocation.isValid()) {
        log.warn("Invalid geolocation for traffic point %s, therefore won't be imported: %s".formatted(trafficPointElementCsvModel.getSloid(), geolocation));
        geolocation = null;
      }
    }

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .designation(trafficPointElementCsvModel.getDesignation())
        .designationOperational(trafficPointElementCsvModel.getDesignationOperational())
        .length(trafficPointElementCsvModel.getLength())
        .boardingAreaHeight(trafficPointElementCsvModel.getBoardingAreaHeight())
        .compassDirection(trafficPointElementCsvModel.getCompassDirection())
        .trafficPointElementType(TrafficPointElementType.fromValue(trafficPointElementCsvModel.getTrafficPointElementType()))
        .servicePointNumber(trafficPointElementCsvModel.getServicePointNumber())
        .sloid(trafficPointElementCsvModel.getSloid())
        .parentSloid(trafficPointElementCsvModel.getParentSloid())
        .validFrom(trafficPointElementCsvModel.getValidFrom())
        .validTo(trafficPointElementCsvModel.getValidTo())
        .creator(trafficPointElementCsvModel.getCreatedBy())
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(trafficPointElementCsvModel.getEditedBy())
        .editionDate(trafficPointElementCsvModel.getEditedAt())
        .trafficPointElementGeolocation(geolocation)
        .build();

    if (Objects.nonNull(geolocation)) {
      geolocation.setTrafficPointElementVersion(trafficPointElementVersion);
    }

    return trafficPointElementVersion;
  }

}
