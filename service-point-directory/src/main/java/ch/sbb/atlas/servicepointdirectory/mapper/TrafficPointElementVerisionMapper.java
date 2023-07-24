package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointElementVerisionMapper {

  public static ReadTrafficPointElementVersionModel toModel(TrafficPointElementVersion trafficPointElementVersion) {
    return ReadTrafficPointElementVersionModel.builder()
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
        .etagVersion(trafficPointElementVersion.getVersion())
        .build();
  }

  public static TrafficPointElementVersion toEntity(CreateTrafficPointElementVersionModel createTrafficPointElementVersionModel) {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(createTrafficPointElementVersionModel.getNumberWithoutCheckDigit());
//    GeolocationBaseEntity geolocationBaseEntity = GeolocationMapper.toEntity(createTrafficPointElementVersionModel.getTrafficPointElementGeolocation());
//    TrafficPointElementGeolocation trafficPointElementGeolocation = new TrafficPointElementGeolocation();
//    if (geolocationBaseEntity != null) {
//      trafficPointElementGeolocation.setEast(geolocationBaseEntity.getEast());
//      trafficPointElementGeolocation.setHeight(geolocationBaseEntity.getHeight());
//      trafficPointElementGeolocation.setNorth(geolocationBaseEntity.getNorth());
//      trafficPointElementGeolocation.setSpatialReference(geolocationBaseEntity.getSpatialReference());
//    }
    return TrafficPointElementVersion.builder()
            .id(createTrafficPointElementVersionModel.getId())
            .sloid(createTrafficPointElementVersionModel.getSloid())
            .designation(createTrafficPointElementVersionModel.getDesignation())
            .designationOperational(createTrafficPointElementVersionModel.getDesignationOperational())
            .length(createTrafficPointElementVersionModel.getLength())
            .boardingAreaHeight(createTrafficPointElementVersionModel.getBoardingAreaHeight())
            .compassDirection(createTrafficPointElementVersionModel.getCompassDirection())
            .trafficPointElementType(createTrafficPointElementVersionModel.getTrafficPointElementType())
            .servicePointNumber(servicePointNumber)
            .parentSloid(createTrafficPointElementVersionModel.getParentSloid())
            .validFrom(createTrafficPointElementVersionModel.getValidFrom())
            .validTo(createTrafficPointElementVersionModel.getValidTo())
            .version(createTrafficPointElementVersionModel.getEtagVersion())
            .trafficPointElementGeolocation(GeolocationMapper.toTrafficPointElementEntity(createTrafficPointElementVersionModel.getTrafficPointElementGeolocation()))
            .creationDate(createTrafficPointElementVersionModel.getCreationDate())
            .creator(createTrafficPointElementVersionModel.getCreator())
            .editionDate(createTrafficPointElementVersionModel.getEditionDate())
            .editor(createTrafficPointElementVersionModel.getEditor())
            .build();
  }

}
