package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTemplateGenerator {

  private static final String DEFAULT_SLOID = "ch:1:sloid:7000";
  private static final LocalDate DEFAULT_VALID_FROM = LocalDate.of(2021, 4, 1);
  private static final LocalDate DEFAULT_VALID_TO = LocalDate.of(2099, 12, 31);
  private static final String DEFAULT_DESIGNATION_OFFICIAL = "Bern";
  private static final String DEFAULT_BUSINESS_ORGANISATION = "ch:1:sboid:1";
  private static final String DEFAULT_DESIGNATION_LONG = "Bern";
  private static final Double DEFAULT_EAST = 2600037.945;
  private static final Double DEFAULT_NORTH = 1199749.812;
  private static final Double DEFAULT_HEIGHT = 540.2;
  private static final String DEFAULT_CODE_OF_DESTINATION_STATION = "1857";

  public static ServicePointUpdateCsvModel getServicePointUpdateCsvModelExample() {
    return ServicePointUpdateCsvModel.builder()
        .sloid(DEFAULT_SLOID)
        .validFrom(DEFAULT_VALID_FROM)
        .validTo(DEFAULT_VALID_TO)
        .designationOfficial(DEFAULT_DESIGNATION_OFFICIAL)
        .freightServicePoint(false)
        .stopPointType(StopPointType.ORDERLY)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .businessOrganisation(DEFAULT_BUSINESS_ORGANISATION)
        .categories(Set.of(Category.HOSTNAME, Category.GALLERY, Category.POINT_OF_SALE))
        .designationLong(DEFAULT_DESIGNATION_LONG)
        .east(DEFAULT_EAST)
        .north(DEFAULT_NORTH)
        .height(DEFAULT_HEIGHT)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.ASSIGNED_OPERATING_POINT)
        .operatingPointType(OperatingPointType.RAILNET_POINT)
        .sortCodeOfDestinationStation(DEFAULT_CODE_OF_DESTINATION_STATION)
        .spatialReference(SpatialReference.LV95)
        .build();
  }

}
