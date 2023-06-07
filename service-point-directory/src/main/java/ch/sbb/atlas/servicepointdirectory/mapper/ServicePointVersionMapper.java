package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.servicepointdirectory.api.CodeAndDesignation;
import ch.sbb.atlas.servicepointdirectory.api.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.ReadServicePointVersionModel;
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

  public static ServicePointVersion toEntity(CreateServicePointVersionModel servicePointVersionModel) {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointVersionModel.getCountryCodeAndServicePointId());
    ServicePointNumber operatingPointKilometerMasterNumber = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointVersionModel.getOperatingPointKilometerMasterNumber());
    return ServicePointVersion.builder()
        .id(servicePointVersionModel.getId())
        .number(servicePointNumber)
        .sloid(servicePointVersionModel.getSloid())
        .numberShort(servicePointNumber.getNumberShort())
        .country(servicePointNumber.getCountry())
        .designationLong(servicePointVersionModel.getDesignationLong())
        .designationOfficial(servicePointVersionModel.getDesignationOfficial())
        .abbreviation(servicePointVersionModel.getAbbreviation())
        .statusDidok3(servicePointVersionModel.getStatusDidok3())
        .sortCodeOfDestinationStation(servicePointVersionModel.getSortCodeOfDestinationStation())
        .businessOrganisation(servicePointVersionModel.getBusinessOrganisation())
        .operatingPointType(servicePointVersionModel.getOperatingPointType())
        .stopPointType(servicePointVersionModel.getStopPointType())
        .status(servicePointVersionModel.getStatus())
        .operatingPointKilometerMaster(operatingPointKilometerMasterNumber)
        .operatingPointRouteNetwork(servicePointVersionModel.isOperatingPointRouteNetwork())
        .comment(servicePointVersionModel.getFotComment())
        .validFrom(servicePointVersionModel.getValidFrom())
        .validTo(servicePointVersionModel.getValidTo())
        .freightServicePoint(servicePointVersionModel.isFreightServicePoint())
        .operatingPoint(servicePointVersionModel.isOperatingPoint())
        .operatingPointWithTimetable(servicePointVersionModel.isOperatingPointWithTimetable())
        .operatingPointTechnicalTimetableType(servicePointVersionModel.getOperatingPointTechnicalTimetableType())
        .operatingPointTrafficPointType(servicePointVersionModel.getOperatingPointTrafficPointType())
        .categories(Set.copyOf(servicePointVersionModel.getCategories()))
        .meansOfTransport(Set.copyOf(servicePointVersionModel.getMeansOfTransport()))
        .servicePointGeolocation(ServicePointGeolocationMapper.toEntity(servicePointVersionModel.getServicePointGeolocation()))
        .build();
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}
