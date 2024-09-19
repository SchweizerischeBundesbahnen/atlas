package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.importservice.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.ImportType;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTemplateGenerator {

  public static Object getServicePointCsvTemplate(BulkImportConfig importConfig) {
    if (Objects.requireNonNull(importConfig.getImportType()) == ImportType.UPDATE) {
      return getServicePointUpdateCsvModelExample();
    }
    throw new BulkImportNotImplementedException(importConfig);
  }

  private static ServicePointUpdateCsvModel getServicePointUpdateCsvModelExample() {
    return ServicePointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
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
  }

}
