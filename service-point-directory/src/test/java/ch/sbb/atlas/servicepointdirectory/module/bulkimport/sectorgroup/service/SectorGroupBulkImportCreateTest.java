package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorGroupBulkImportCreateTest {

  @Test
  void shouldMapFromCsvToCreateModel() {
    BulkImportUpdateContainer<SectorGroupCreateCsvModel> container =
        BulkImportUpdateContainer.<SectorGroupCreateCsvModel>builder()
            .object(SectorGroupCreateCsvModel.builder()
                .trafficPointSloid("ch:1:sloid:7000:1:2")
                .validFrom(LocalDate.of(2021, 4, 1))
                .validTo(LocalDate.of(2099, 12, 31))
                .designation("AB")
                .length(35.0)
                .sectorSloids(Set.of("ch:1:sloid:7000:1:2:1", "ch:1:sloid:7000:1:2:2"))
                .build())
            .build();

    CreateSectorGroupVersionModel expected = CreateSectorGroupVersionModel.builder()
        .trafficPointSloid("ch:1:sloid:7000:1:2")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("AB")
        .length(35.0)
        .sectorSloids(Set.of("ch:1:sloid:7000:1:2:1", "ch:1:sloid:7000:1:2:2"))
        .build();

    CreateSectorGroupVersionModel result = SectorGroupBulkImportCreate.apply(container);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldNotSetIdSoThatCreateIdCheckPasses() {
    BulkImportUpdateContainer<SectorGroupCreateCsvModel> container =
        BulkImportUpdateContainer.<SectorGroupCreateCsvModel>builder()
            .object(SectorGroupCreateCsvModel.builder()
                .trafficPointSloid("ch:1:sloid:7000:1:2")
                .validFrom(LocalDate.of(2021, 4, 1))
                .validTo(LocalDate.of(2099, 12, 31))
                .designation("AB")
                .sectorSloids(Set.of("ch:1:sloid:7000:1:2:1", "ch:1:sloid:7000:1:2:2"))
                .build())
            .build();

    CreateSectorGroupVersionModel result = SectorGroupBulkImportCreate.apply(container);

    assertThat(result.getId()).isNull();
    assertThat(result.getSloid()).isNull();
  }

}
