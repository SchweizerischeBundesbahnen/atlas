package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import java.util.Optional;
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

  public static ServicePointVersion toEntity(UpdateServicePointVersionModel updateServicePointVersionModel,
      ServicePointNumber servicePointNumber) {
    ServicePointNumber operatingPointKilometerMasterNumber =
        updateServicePointVersionModel.isOperatingPointRouteNetwork() ? servicePointNumber :
            Optional.ofNullable(updateServicePointVersionModel.getOperatingPointKilometerMasterNumber())
                .map(ServicePointNumber::ofNumberWithoutCheckDigit)
                .orElse(null);

    ServicePointVersion servicePointVersion = ServicePointVersion.builder()
        .id(updateServicePointVersionModel.getId())
        .designationLong(updateServicePointVersionModel.getDesignationLong())
        .designationOfficial(updateServicePointVersionModel.getDesignationOfficial())
        .abbreviation(updateServicePointVersionModel.getAbbreviation())
        .sortCodeOfDestinationStation(updateServicePointVersionModel.getSortCodeOfDestinationStation())
        .businessOrganisation(updateServicePointVersionModel.getBusinessOrganisation())
        .operatingPointType(updateServicePointVersionModel.getOperatingPointType())
        .stopPointType(updateServicePointVersionModel.getStopPointType())
        .operatingPointKilometerMaster(operatingPointKilometerMasterNumber)
        .operatingPointRouteNetwork(updateServicePointVersionModel.isOperatingPointRouteNetwork())
        .validFrom(updateServicePointVersionModel.getValidFrom())
        .validTo(updateServicePointVersionModel.getValidTo())
        .freightServicePoint(updateServicePointVersionModel.isFreightServicePoint())
        .operatingPoint(updateServicePointVersionModel.isOperatingPoint())
        .operatingPointWithTimetable(updateServicePointVersionModel.isOperatingPointWithTimetable())
        .operatingPointTechnicalTimetableType(updateServicePointVersionModel.getOperatingPointTechnicalTimetableType())
        .operatingPointTrafficPointType(updateServicePointVersionModel.getOperatingPointTrafficPointType())
        .categories(Set.copyOf(updateServicePointVersionModel.getCategories()))
        .meansOfTransport(Set.copyOf(updateServicePointVersionModel.getMeansOfTransport()))
        .servicePointGeolocation(
            ServicePointGeolocationMapper.toEntity(updateServicePointVersionModel.getServicePointGeolocation()))
        .version(updateServicePointVersionModel.getEtagVersion())
        .editor(updateServicePointVersionModel.getEditor())
        .editionDate(updateServicePointVersionModel.getEditionDate())
        .creator(updateServicePointVersionModel.getCreator())
        .creationDate(updateServicePointVersionModel.getCreationDate())
        .build();

    servicePointVersion.setNumber(servicePointNumber);
    servicePointVersion.setSloid(ServicePointNumber.calculateSloid(servicePointNumber));
    servicePointVersion.setNumberShort(servicePointNumber.getNumberShort());
    servicePointVersion.setCountry(servicePointNumber.getCountry());

    return servicePointVersion;
  }

  private static List<MeanOfTransport> getMeansOfTransportSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getMeansOfTransport().stream().sorted().toList();
  }

  private static List<Category> getCategoriesSorted(ServicePointVersion servicePointVersion) {
    return servicePointVersion.getCategories().stream().sorted().toList();
  }

}
