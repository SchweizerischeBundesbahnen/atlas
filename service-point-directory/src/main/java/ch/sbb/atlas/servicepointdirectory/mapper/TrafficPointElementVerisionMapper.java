package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.TrafficPointElementVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointElementVerisionMapper {

  public static TrafficPointElementVersionModel fromEntity(TrafficPointElementVersion trafficPointElementVersion) {
    return TrafficPointElementVersionModel.builder()
        .id(trafficPointElementVersion.getId())
        .sloid(trafficPointElementVersion.getSloid())
        .designation(trafficPointElementVersion.getDesignation())
        .designationOperational(trafficPointElementVersion.getDesignationOperational())
        .length(trafficPointElementVersion.getLength())
        .boardingAreaHeight(trafficPointElementVersion.getBoardingAreaHeight())
        .compassDirection(trafficPointElementVersion.getCompassDirection())
        .trafficPointElementType(trafficPointElementVersion.getTrafficPointElementType())
        .servicePointNumber(trafficPointElementVersion.getServicePointNumber())
        .parentSloid(trafficPointElementVersion.getParentSloid())
        .validFrom(trafficPointElementVersion.getValidFrom())
        .validTo(trafficPointElementVersion.getValidTo())
        .trafficPointElementGeolocation(GeolocationMapper.toModel(trafficPointElementVersion.getTrafficPointElementGeolocation()))
        .creationDate(trafficPointElementVersion.getCreationDate())
        .creator(trafficPointElementVersion.getCreator())
        .editionDate(trafficPointElementVersion.getEditionDate())
        .editor(trafficPointElementVersion.getEditor())
        .build();
  }

}
