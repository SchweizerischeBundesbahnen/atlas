package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import java.util.Optional;
import java.util.function.Function;

public class TrafficPointElementCsvToEntityMapper implements Function<TrafficPointElementCsvModel, TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion apply(TrafficPointElementCsvModel trafficPointElementCsvModel) {
    //TODO: fallback entfernen, wenn in csv geliefert
    String createdBy = Optional.ofNullable(trafficPointElementCsvModel.getCreatedBy()).orElse("fxatlsy");
    String editedBy = Optional.ofNullable(trafficPointElementCsvModel.getEditedBy()).orElse("fxatlsy");

    TrafficPointElementGeolocation geolocation = TrafficPointElementGeolocation.builder()
        .source_spatial_ref(1) //TODO: Marek liefert dies dann im CSV
        .lv03east(trafficPointElementCsvModel.getELv03())
        .lv03north(trafficPointElementCsvModel.getNLv03())
        .lv95east(trafficPointElementCsvModel.getELv95())
        .lv95north(trafficPointElementCsvModel.getNLv95())
        .wgs84east(trafficPointElementCsvModel.getEWgs84())
        .wgs84north(trafficPointElementCsvModel.getNWgs84())
        .height(trafficPointElementCsvModel.getHeight())
        .creator(createdBy)
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(editedBy)
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
        .creator(createdBy)
        .creationDate(trafficPointElementCsvModel.getCreatedAt())
        .editor(editedBy)
        .editionDate(trafficPointElementCsvModel.getEditedAt())
        .trafficPointElementGeolocation(geolocation)
        .build();

    geolocation.setTrafficPointElementVersion(trafficPointElementVersion);

    return trafficPointElementVersion;
  }

}
