package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.servicepointdirectory.api.model.CodeAndDesignation;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.util.List;
import java.util.Set;
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
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(createServicePointVersionModel.getNumberWithoutCheckDigit());
    ServicePointNumber operatingPointKilometerMasterNumber = ServicePointNumber.ofNumberWithoutCheckDigit(createServicePointVersionModel.getOperatingPointKilometerMasterNumber());
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
        .servicePointGeolocation(ServicePointGeolocationMapper.toEntity(createServicePointVersionModel.getServicePointGeolocation()))
        .build();
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}
