package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
class SectorGroupTemplateGenerator {

  private static final Set<String> SECTOR_SLOIDS = new LinkedHashSet<>(
      List.of("ch:1:sloid:7000:1:1:1", "ch:1:sloid:7000:1:1:2"));

  static final SectorGroupCreateCsvModel SECTOR_GROUP_CREATE_CSV_MODEL = SectorGroupCreateCsvModel.builder()
      .trafficPointSloid("ch:1:sloid:7000:1:1")
      .validFrom(LocalDate.of(2026, 1, 1))
      .validTo(LocalDate.of(2026, 12, 31))
      .designation("AB")
      .length(35.0)
      .sectorSloids(SECTOR_SLOIDS)
      .build();

}
