package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrafficPointElementCsvToEntityMapper implements
    Function<TrafficPointElementCsvModel, TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion apply(TrafficPointElementCsvModel trafficPointElementCsvModel) {
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation(trafficPointElementCsvModel.getDesignation())
        .designationOperational(trafficPointElementCsvModel.getDesignationOperational())
        .length(trafficPointElementCsvModel.getLength())
        .boardingAreaHeight(trafficPointElementCsvModel.getBoardingAreaHeight())
        .compassDirection(trafficPointElementCsvModel.getCompassDirection())
        .trafficPointElementType(TrafficPointElementType.fromValue(
            trafficPointElementCsvModel.getTrafficPointElementType()))
        .servicePointNumber(trafficPointElementCsvModel.getServicePointNumber())
        .sloid(trafficPointElementCsvModel.getSloid())
        .parentSloid(trafficPointElementCsvModel.getParentSloid())
        .validFrom(trafficPointElementCsvModel.getValidFrom())
        .validTo(trafficPointElementCsvModel.getValidTo())
        .creator(trafficPointElementCsvModel.getCreatedBy())
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(trafficPointElementCsvModel.getEditedBy())
        .editionDate(trafficPointElementCsvModel.getEditedAt())
        .build();

    TrafficPointElementGeolocation geolocation = TrafficPointElementGeolocation
        .builder()
        .country(Country.from(trafficPointElementCsvModel.getCountry()))
        .locationTypes(LocationTypes
            .builder()
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

    if (geolocation.isValid()) {
      trafficPointElementVersion.setTrafficPointElementGeolocation(geolocation);
      geolocation.setTrafficPointElementVersion(trafficPointElementVersion);
    }

    return trafficPointElementVersion;
  }
}
