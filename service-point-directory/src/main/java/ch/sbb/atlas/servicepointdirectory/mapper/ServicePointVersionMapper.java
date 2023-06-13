package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CodeAndDesignation;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointGeolocationModel.Canton;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointGeolocationModel.DistrictModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointGeolocationModel.LocalityMunicipalityModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointGeolocationModel.SwissLocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointVersionMapper {

  public static ReadServicePointVersionModel toModel(ServicePointVersion servicePointVersion) {
    return ReadServicePointVersionModel.builder()
        .id(servicePointVersion.getId())
        .number(servicePointVersion.getNumber())
        .sloid(servicePointVersion.getSloid())
        .designationLong(servicePointVersion.getDesignationLong())
        .designationOfficial(servicePointVersion.getDesignationOfficial())
        .abbreviation(servicePointVersion.getAbbreviation())
        .statusDidok3(servicePointVersion.getStatusDidok3())
        .statusDidok3Information(CodeAndDesignation.fromEnum(servicePointVersion.getStatusDidok3()))
        .sortCodeOfDestinationStation(servicePointVersion.getSortCodeOfDestinationStation())
        .businessOrganisation(servicePointVersion.getBusinessOrganisation())
        .operatingPointType(servicePointVersion.getOperatingPointType())
        .operatingPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointType()))
        .stopPointType(servicePointVersion.getStopPointType())
        .stopPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getStopPointType()))
        .status(servicePointVersion.getStatus())
        .operatingPointKilometerMaster(servicePointVersion.getOperatingPointKilometerMaster())
        .operatingPointRouteNetwork(servicePointVersion.isOperatingPointRouteNetwork())
        .fotComment(servicePointVersion.getComment())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .freightServicePoint(servicePointVersion.isFreightServicePoint())
        .operatingPoint(servicePointVersion.isOperatingPoint())
        .operatingPointWithTimetable(servicePointVersion.isOperatingPointWithTimetable())
        .operatingPointTechnicalTimetableType(servicePointVersion.getOperatingPointTechnicalTimetableType())
        .operatingPointTechnicalTimetableTypeInformation(
            CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointTechnicalTimetableType()))
        .operatingPointTrafficPointType(servicePointVersion.getOperatingPointTrafficPointType())
        .operatingPointTrafficPointTypeInformation(
            CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointTrafficPointType()))
        .categories(getCategoriesSorted(servicePointVersion))
        .categoriesInformation(getCategoriesSorted(servicePointVersion).stream().map(CodeAndDesignation::fromEnum).toList())
        .meansOfTransport(getMeansOfTransportSorted(servicePointVersion))
        .meansOfTransportInformation(
            getMeansOfTransportSorted(servicePointVersion).stream().map(CodeAndDesignation::fromEnum).toList())
        .servicePointGeolocation(ServicePointGeolocationMapper.toModel(servicePointVersion.getServicePointGeolocation()))
        .creationDate(servicePointVersion.getCreationDate())
        .creator(servicePointVersion.getCreator())
        .editionDate(servicePointVersion.getEditionDate())
        .editor(servicePointVersion.getEditor())
        .build();
  }

  public static ServicePointVersion toEntity(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(
        createServicePointVersionModel.getNumberWithoutCheckDigit());
    ServicePointNumber operatingPointKilometerMasterNumber = ServicePointNumber.ofNumberWithoutCheckDigit(
        createServicePointVersionModel.getOperatingPointKilometerMasterNumber());
    return ServicePointVersion.builder()
        .id(createServicePointVersionModel.getId())
        .number(servicePointNumber)
        .sloid(createServicePointVersionModel.getSloid())
        .numberShort(servicePointNumber.getNumberShort())
        .country(servicePointNumber.getCountry())
        .designationLong(createServicePointVersionModel.getDesignationLong())
        .designationOfficial(createServicePointVersionModel.getDesignationOfficial())
        .abbreviation(createServicePointVersionModel.getAbbreviation())
        .statusDidok3(createServicePointVersionModel.getStatusDidok3())
        .sortCodeOfDestinationStation(createServicePointVersionModel.getSortCodeOfDestinationStation())
        .businessOrganisation(createServicePointVersionModel.getBusinessOrganisation())
        .operatingPointType(createServicePointVersionModel.getOperatingPointType())
        .stopPointType(createServicePointVersionModel.getStopPointType())
        .status(createServicePointVersionModel.getStatus())
        .operatingPointKilometerMaster(operatingPointKilometerMasterNumber)
        .operatingPointRouteNetwork(createServicePointVersionModel.isOperatingPointRouteNetwork())
        .comment(createServicePointVersionModel.getFotComment())
        .validFrom(createServicePointVersionModel.getValidFrom())
        .validTo(createServicePointVersionModel.getValidTo())
        .freightServicePoint(createServicePointVersionModel.isFreightServicePoint())
        .operatingPoint(createServicePointVersionModel.isOperatingPoint())
        .operatingPointWithTimetable(createServicePointVersionModel.isOperatingPointWithTimetable())
        .operatingPointTechnicalTimetableType(createServicePointVersionModel.getOperatingPointTechnicalTimetableType())
        .operatingPointTrafficPointType(createServicePointVersionModel.getOperatingPointTrafficPointType())
        .categories(Set.copyOf(createServicePointVersionModel.getCategories()))
        .meansOfTransport(Set.copyOf(createServicePointVersionModel.getMeansOfTransport()))
        .servicePointGeolocation(
            ServicePointGeolocationMapper.toEntity(createServicePointVersionModel.getServicePointGeolocation()))
        .build();
  }

  public static ServicePointGeolocationModel fromEntity(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = GeolocationModelMapper.getTransformedCoordinates(servicePointGeolocation);
    return ServicePointGeolocationModel.builder()
        .country(servicePointGeolocation.getCountry())
        .swissLocation(SwissLocation.builder()
            .canton(servicePointGeolocation.getSwissCanton())
            .cantonInformation(getCanton(servicePointGeolocation))
            .district(DistrictModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissDistrictNumber())
                .districtName(servicePointGeolocation.getSwissDistrictName())
                .build())
            .localityMunicipality(LocalityMunicipalityModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissMunicipalityNumber())
                .municipalityName(servicePointGeolocation.getSwissMunicipalityName())
                .localityName(servicePointGeolocation.getSwissLocalityName())
                .build())
            .build())
        .spatialReference(servicePointGeolocation.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .wgs84web(coordinates.get(SpatialReference.WGS84WEB))
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  private static Canton getCanton(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation.getSwissCanton() == null) {
      return null;
    }
    return Canton.builder()
        .abbreviation(servicePointGeolocation.getSwissCanton().getAbbreviation())
        .fsoNumber(servicePointGeolocation.getSwissCanton().getNumber())
        .name(servicePointGeolocation.getSwissCanton().getName())
        .build();
  }

  public static Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
    Map<SpatialReference, CoordinatePair> coordinates = new EnumMap<>(SpatialReference.class);

    CoordinateTransformer coordinateTransformer = new CoordinateTransformer();
    Stream.of(SpatialReference.values()).forEach(spatialReference -> {
      if (spatialReference == entity.getSpatialReference()) {
        coordinates.put(spatialReference, entity.asCoordinatePair());
      } else {
        coordinates.put(spatialReference, coordinateTransformer.transform(entity.asCoordinatePair(), spatialReference));
      }
    });
    return coordinates;
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}
