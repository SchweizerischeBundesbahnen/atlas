package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.Optional;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;

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
        .sortCodeOfDestinationStation(servicePointVersion.getSortCodeOfDestinationStation())
        .businessOrganisation(servicePointVersion.getBusinessOrganisation())
        .operatingPointType(servicePointVersion.getOperatingPointType())
        .stopPointType(servicePointVersion.getStopPointType())
        .status(servicePointVersion.getStatus())
        .operatingPointKilometerMaster(servicePointVersion.getOperatingPointKilometerMaster())
        .operatingPointRouteNetwork(servicePointVersion.isOperatingPointRouteNetwork())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .freightServicePoint(servicePointVersion.isFreightServicePoint())
        .operatingPoint(servicePointVersion.isOperatingPoint())
        .operatingPointWithTimetable(servicePointVersion.isOperatingPointWithTimetable())
        .operatingPointTechnicalTimetableType(servicePointVersion.getOperatingPointTechnicalTimetableType())
        .operatingPointTrafficPointType(servicePointVersion.getOperatingPointTrafficPointType())
        .categories(getCategoriesSorted(servicePointVersion))
        .meansOfTransport(getMeansOfTransportSorted(servicePointVersion))
        .servicePointGeolocation(ServicePointGeolocationMapper.toModel(servicePointVersion.getServicePointGeolocation()))
        .creationDate(servicePointVersion.getCreationDate())
        .creator(servicePointVersion.getCreator())
        .editionDate(servicePointVersion.getEditionDate())
        .editor(servicePointVersion.getEditor())
        .etagVersion(servicePointVersion.getVersion())
        .build();
  }

  public static ServicePointVersion toEntity(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(
        createServicePointVersionModel.getNumberWithoutCheckDigit());

    ServicePointNumber operatingPointKilometerMasterNumber =
        Optional.ofNullable(createServicePointVersionModel.getOperatingPointKilometerMasterNumber())
            .map(ServicePointNumber::ofNumberWithoutCheckDigit)
            .orElse(null);

    return ServicePointVersion.builder()
        .id(createServicePointVersionModel.getId())
        .number(servicePointNumber)
        .sloid(createServicePointVersionModel.getSloid())
        .numberShort(servicePointNumber.getNumberShort())
        .country(servicePointNumber.getCountry())
        .designationLong(createServicePointVersionModel.getDesignationLong())
        .designationOfficial(createServicePointVersionModel.getDesignationOfficial())
        .abbreviation(createServicePointVersionModel.getAbbreviation())
        .sortCodeOfDestinationStation(createServicePointVersionModel.getSortCodeOfDestinationStation())
        .businessOrganisation(createServicePointVersionModel.getBusinessOrganisation())
        .operatingPointType(createServicePointVersionModel.getOperatingPointType())
        .stopPointType(createServicePointVersionModel.getStopPointType())
        .operatingPointKilometerMaster(operatingPointKilometerMasterNumber)
        .operatingPointRouteNetwork(createServicePointVersionModel.isOperatingPointRouteNetwork())
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
        .version(createServicePointVersionModel.getEtagVersion())
        .build();
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}


