package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.enumeration.Category;
import ch.sbb.exportservice.entity.enumeration.MeanOfTransport;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.ServicePointVersionCsvModelBuilder;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointVersionProcessor implements ItemProcessor<ServicePointVersion, ServicePointVersionCsvModel> {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy mm:ss");

  @Override
  public ServicePointVersionCsvModel process(ServicePointVersion version) {
    ServicePointVersionCsvModelBuilder builder = ServicePointVersionCsvModel.builder();
    builder.numberShort(version.getNumber().getNumberShort());
    builder.uicCountryCode(version.getCountry().getUicCode());
    builder.sloid(version.getSloid());
    builder.number(version.getNumber().getValue());
    builder.checkDigit(version.getNumber().getCheckDigit());
    builder.validFrom(DATE_FORMATTER.format(version.getValidFrom()));
    builder.validTo(DATE_FORMATTER.format(version.getValidTo()));
    builder.designationOfficial(version.getDesignationOfficial());
    builder.designationLong(version.getDesignationLong());
    builder.abbreviation(version.getAbbreviation());
    builder.operatingPoint(version.isOperatingPoint());
    builder.operatingPointWithTimetable(version.isOperatingPointWithTimetable());
    builder.stopPoint(version.isOperatingPoint());
    builder.stopPointTypeCode(version.getStopPointType() != null ? version.getStopPointType().getCode() : null);
    builder.freightServicePoint(version.isFreightServicePoint());
    builder.trafficPoint(version.isTrafficPoint());
    builder.borderPoint(version.isBorderPoint());
    builder.hasGeolocation(version.hasGeolocation());
    builder.isoCoutryCode(version.getCountry().getIsoCode());
    if (version.getServicePointGeolocation() != null) {
      builder.cantonAbbreviation(version.getServicePointGeolocation().getSwissCanton() != null ?
          version.getServicePointGeolocation().getSwissCanton().getAbbreviation() : null);
      builder.districtName(version.getServicePointGeolocation().getSwissDistrictName());
      builder.districtFsoName(version.getServicePointGeolocation().getSwissDistrictNumber());
      builder.municipalityName(version.getServicePointGeolocation().getSwissMunicipalityName());
      builder.fsoNumber(version.getServicePointGeolocation().getSwissMunicipalityNumber());
      builder.localityName(version.getServicePointGeolocation().getSwissLocalityName());
    }
    builder.operatingPointTypeCode(version.getOperatingPointType() != null ?
        version.getOperatingPointType().getCode() : null);
    builder.operatingPointTechnicalTimetableTypeCode(version.getOperatingPointTechnicalTimetableType() != null ?
        version.getOperatingPointTechnicalTimetableType().getCode() : null);
    builder.meansOfTransportCode(
        version.getMeansOfTransport().stream().map(MeanOfTransport::name).collect(Collectors.joining("|")));
    builder.categoriesCode(version.getCategories().stream().map(Category::name).collect(Collectors.joining("|")));
    builder.operatingPointTrafficPointTypeCode(version.getOperatingPointTrafficPointType() != null ?
        version.getOperatingPointTrafficPointType().getCode() : null);
    builder.operatingPointRouteNetwork(version.isOperatingPointRouteNetwork());
    builder.operatingPointKilometer(version.isOperatingPointKilometer());
    builder.operatingPointKilometerMasterNumber(version.getOperatingPointKilometerMaster() != null ?
        version.getOperatingPointKilometerMaster().getNumber() : null);
    builder.sortCodeOfDestinationStation(version.getSortCodeOfDestinationStation());
    builder.sboid(version.getBusinessOrganisation());
    builder.fotComment(version.getComment());
    //TODO: add geolocation

    builder.creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()));
    builder.editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()));
    builder.statusDidok3(version.getStatusDidok3().name());
    return builder.build();
  }

}
