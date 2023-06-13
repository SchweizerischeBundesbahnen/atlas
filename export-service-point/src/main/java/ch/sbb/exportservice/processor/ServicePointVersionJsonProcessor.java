package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.servicepoint.CodeAndDesignation;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.exportservice.entity.ServicePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointVersionJsonProcessor extends BaseProcessor implements ItemProcessor<ServicePointVersion,
    ServicePointVersionModel> {

  @Override
  public ServicePointVersionModel process(ServicePointVersion servicePointVersion) {

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
        .servicePointGeolocation(servicePointVersion.getServicePointGeolocation() != null ?
            fromEntity(servicePointVersion.getServicePointGeolocation()) : null)
        .status(servicePointVersion.getStatus())
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .creationDate(servicePointVersion.getCreationDate())
        .creator(servicePointVersion.getCreator())
        .editionDate(servicePointVersion.getEditionDate())
        .editor(servicePointVersion.getEditor())
        .build();
  }

}
