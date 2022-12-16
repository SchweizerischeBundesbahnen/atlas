package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import java.util.function.Function;

public class TrafficPointElementCsvToEntityMapper implements Function<TrafficPointElementCsvModel, TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion apply(TrafficPointElementCsvModel trafficPointElementCsvModel) {
    TrafficPointElementGeolocation geolocation = TrafficPointElementGeolocation.builder()
        .spatialReference(trafficPointElementCsvModel.getSpatialReference())
        .country(Country.from(trafficPointElementCsvModel.getCountry()))
        .lv03east(trafficPointElementCsvModel.getELv03())
        .lv03north(trafficPointElementCsvModel.getNLv03())
        .lv95east(trafficPointElementCsvModel.getELv95())
        .lv95north(trafficPointElementCsvModel.getNLv95())
        .wgs84east(trafficPointElementCsvModel.getEWgs84())
        .wgs84north(trafficPointElementCsvModel.getNWgs84())
        .height(trafficPointElementCsvModel.getHeight())
        .creator(trafficPointElementCsvModel.getCreatedBy())
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(trafficPointElementCsvModel.getEditedBy())
        .editionDate(trafficPointElementCsvModel.getEditedAt())
        .build();

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

    geolocation.setTrafficPointElementVersion(trafficPointElementVersion);

    return trafficPointElementVersion;
  }

}
