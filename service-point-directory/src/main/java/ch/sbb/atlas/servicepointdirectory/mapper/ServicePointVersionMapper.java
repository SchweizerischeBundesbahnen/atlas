package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.servicepointdirectory.api.CodeAndDesignation;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointVersionMapper {

  public static ServicePointVersionModel fromEntity(ServicePointVersion servicePointVersion) {
    return ServicePointVersionModel.builder()
        .id(servicePointVersion.getId())
        .number(servicePointVersion.getNumber())
        .sloid(servicePointVersion.getSloid())
        .designationLong(servicePointVersion.getDesignationLong())
        .designationOfficial(servicePointVersion.getDesignationOfficial())
        .abbreviation(servicePointVersion.getAbbreviation())
        .statusDidok3(servicePointVersion.getStatusDidok3())
        .statusDidok3Information(CodeAndDesignation.fromEnum(servicePointVersion.getStatusDidok3()))
        .operatingPoint(servicePointVersion.isOperatingPoint())
        .operatingPointWithTimetable(servicePointVersion.isOperatingPointWithTimetable())
        .freightServicePoint(servicePointVersion.isFreightServicePoint())
        .sortCodeOfDestinationStation(servicePointVersion.getSortCodeOfDestinationStation())
        .businessOrganisation(servicePointVersion.getBusinessOrganisation())
        .categories(getCategoriesSorted(servicePointVersion))
        .categoriesInformation(getCategoriesSorted(servicePointVersion).stream().map(CodeAndDesignation::fromEnum).toList())
        .operatingPointType(servicePointVersion.getOperatingPointType())
        .operatingPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointType()))
        .operatingPointTechnicalTimetableType(servicePointVersion.getOperatingPointTechnicalTimetableType())
        .operatingPointTechnicalTimetableTypeInformation(
            CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointTechnicalTimetableType()))
        .operatingPointTrafficPointType(servicePointVersion.getOperatingPointTrafficPointType())
        .operatingPointTrafficPointTypeInformation(
            CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointTrafficPointType()))
        .operatingPointRouteNetwork(servicePointVersion.isOperatingPointRouteNetwork())
        .operatingPointKilometerMaster(servicePointVersion.getOperatingPointKilometerMaster())
        .meansOfTransport(getMeansOfTransportSorted(servicePointVersion))
        .meansOfTransportInformation(
            getMeansOfTransportSorted(servicePointVersion).stream().map(CodeAndDesignation::fromEnum).toList())
        .stopPointType(servicePointVersion.getStopPointType())
        .stopPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getStopPointType()))
        .fotComment(servicePointVersion.getComment())
        .servicePointGeolocation(ServicePointGeolocationModel.fromEntity(servicePointVersion.getServicePointGeolocation()))
        .status(servicePointVersion.getStatus())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .creationDate(servicePointVersion.getCreationDate())
        .creator(servicePointVersion.getCreator())
        .editionDate(servicePointVersion.getEditionDate())
        .editor(servicePointVersion.getEditor())
        .build();
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}
