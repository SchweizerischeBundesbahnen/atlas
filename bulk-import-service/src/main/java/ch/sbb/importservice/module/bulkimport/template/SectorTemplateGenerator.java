package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
class SectorTemplateGenerator {

  static final SectorCreateCsvModel SECTOR_CREATE_CSV_MODEL = SectorCreateCsvModel.builder()
      .trafficPointSloid("ch:1:sloid:7000:1:1")
      .validFrom(LocalDate.of(2026, 1, 1))
      .validTo(LocalDate.of(2026, 12, 31))
      .designation("A")
      .length(17.5)
      .east(2600037.945)
      .north(1199749.812)
      .spatialReference(SpatialReference.LV95)
      .height(540.2)
      .edgeHeight(16.0)
      .build();

  static final SectorUpdateCsvModel SECTOR_UPDATE_CSV_MODEL = SectorUpdateCsvModel.builder()
      .sloid("ch:1:sloid:7000:1:1:1")
      .validFrom(LocalDate.of(2026, 1, 1))
      .validTo(LocalDate.of(2026, 12, 31))
      .designation("A")
      .east(2600037.945)
      .north(1199749.812)
      .spatialReference(SpatialReference.LV95)
      .height(540.2)
      .length(17.5)
      .edgeHeight(16.0)
      .build();

}
