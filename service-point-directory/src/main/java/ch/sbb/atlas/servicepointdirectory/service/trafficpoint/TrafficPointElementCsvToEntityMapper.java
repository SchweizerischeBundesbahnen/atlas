package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
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
        .servicePointNumber(ServicePointNumber.of(trafficPointElementCsvModel.getServicePointNumber()))
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
