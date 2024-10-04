package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.importservice.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.ImportType;
import java.time.LocalDate;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointTemplateGenerator {

  public static Object getTrafficPointCsvTemplate(BulkImportConfig importConfig) {
    if (Objects.requireNonNull(importConfig.getImportType() == ImportType.UPDATE)) {
      return getTrafficPointUpdateCsvModelExample();
    }
    throw new BulkImportNotImplementedException(importConfig);
  }

  private static TrafficPointUpdateCsvModel getTrafficPointUpdateCsvModelExample() {
    return TrafficPointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:23")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("Perron 3")
        .designationOperational("CAMPSTR2")
        .length(12.000)
        .boardingAreaHeight(16.00)
        .compassDirection(278.00)
        .east(2600037.945)
        .north(1199749.812)
        .spatialReference(SpatialReference.LV95)
        .height(540.2)
        .parentSloid("ch:1:sloid:7000")
        .build();
  }

}
