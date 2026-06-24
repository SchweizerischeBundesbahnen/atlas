package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
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
class ServicePointTemplateGenerator {

  private static final String DEFAULT_SLOID = "ch:1:sloid:7000";
  private static final Integer DEFAULT_NUMBER = 8512345;
  private static final LocalDate DEFAULT_VALID_TO = LocalDate.of(2099, 12, 31);

  static final ServicePointUpdateCsvModel SERVICE_POINT_UPDATE_CSV_MODEL = ServicePointUpdateCsvModel.builder()
      .sloid(DEFAULT_SLOID)
      .validFrom(LocalDate.of(2021, 4, 1))
      .validTo(DEFAULT_VALID_TO)
      .designationOfficial("Bern")
      .freightServicePoint(false)
      .stopPointType(StopPointType.ORDERLY)
      .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
      .businessOrganisation("ch:1:sboid:1")
      .categories(Set.of(Category.HOSTNAME, Category.GALLERY, Category.POINT_OF_SALE))
      .designationLong("Bern")
      .east(2600037.945)
      .north(1199749.812)
      .height(540.2)
      .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
      .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.ASSIGNED_OPERATING_POINT)
      .operatingPointType(OperatingPointType.RAILNET_POINT)
      .sortCodeOfDestinationStation("1857")
      .spatialReference(SpatialReference.LV95)
      .build();

  static final ServicePointCreateCsvModel SERVICE_POINT_CREATE_CSV_MODEL = ServicePointCreateCsvModel.builder()
      .numberShort(7000)
      .uicCountryCode(85)
      .validFrom(LocalDate.of(2021, 4, 1))
      .validTo(DEFAULT_VALID_TO)
      .designationOfficial("Bern")
      .designationLong("Bern")
      .stopPointType(StopPointType.ORDERLY)
      .freightServicePoint(false)
      .operatingPointType(OperatingPointType.RAILNET_POINT)
      .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.ASSIGNED_OPERATING_POINT)
      .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
      .categories(Set.of(Category.HOSTNAME, Category.GALLERY, Category.POINT_OF_SALE))
      .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
      .sortCodeOfDestinationStation("1857")
      .businessOrganisation("ch:1:sboid:1")
      .east(2600037.945)
      .north(1199749.812)
      .height(540.2)
      .spatialReference(SpatialReference.LV95)
      .build();

  static final ServicePointTerminateCsvModel SERVICE_POINT_TERMINATE_CSV_MODEL = ServicePointTerminateCsvModel.builder()
      .sloid(DEFAULT_SLOID)
      .number(DEFAULT_NUMBER)
      .validTo(DEFAULT_VALID_TO)
      .build();

}
