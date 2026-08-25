package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class SectorBulkImportUpdateTest {

  @Test
  void shouldMapFromCsvToUpdateModel() {
    SectorVersion currentVersion = SectorTestData.getBasicSectorVersion();
    currentVersion.setId(1234L);
    currentVersion.setVersion(3);

    BulkImportUpdateContainer<SectorUpdateCsvModel> container =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid(currentVersion.getSloid())
                .validFrom(LocalDate.of(2021, 4, 1))
                .validTo(LocalDate.of(2099, 12, 31))
                .designation("B")
                .east(2600037.945)
                .north(1199749.812)
                .spatialReference(SpatialReference.LV95)
                .height(540.2)
                .length(12.0)
                .edgeHeight(11.0)
                .build())
            .build();

    CreateSectorVersionModel expected = CreateSectorVersionModel.builder()
        .id(1234L)
        .etagVersion(3)
        .sloid(currentVersion.getSloid())
        .trafficPointSloid(currentVersion.getTrafficPointSloid())
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("B")
        .length(12.0)
        .edgeHeight(11.0)
        .sectorGeolocation(GeolocationBaseCreateModel.builder()
            .east(2600037.945)
            .north(1199749.812)
            .spatialReference(SpatialReference.LV95)
            .height(540.2)
            .build())
        .build();

    CreateSectorVersionModel result = SectorBulkImportUpdate.apply(container, currentVersion);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldTakeValuesFromCurrentVersionWhenNotGiven() {
    SectorVersion currentVersion = SectorTestData.getBasicSectorVersion();
    currentVersion.setId(1234L);
    currentVersion.setVersion(3);

    BulkImportUpdateContainer<SectorUpdateCsvModel> container =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid(currentVersion.getSloid())
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2023, 1, 1))
                .build())
            .build();

    CreateSectorVersionModel result = SectorBulkImportUpdate.apply(container, currentVersion);

    assertThat(result.getDesignation()).isEqualTo(currentVersion.getDesignation());
    assertThat(result.getLength()).isEqualTo(currentVersion.getLength());
    assertThat(result.getEdgeHeight()).isEqualTo(currentVersion.getEdgeHeight());
    assertThat(result.getSectorGeolocation().getNorth()).isEqualTo(currentVersion.getNorth());
    assertThat(result.getSectorGeolocation().getEast()).isEqualTo(currentVersion.getEast());
    assertThat(result.getSectorGeolocation().getHeight()).isEqualTo(currentVersion.getHeight());
    assertThat(result.getSectorGeolocation().getSpatialReference()).isEqualTo(currentVersion.getSpatialReference());
  }

  @Test
  void shouldNullAttributesMarkedAsNulling() {
    SectorVersion currentVersion = SectorTestData.getBasicSectorVersion();
    currentVersion.setId(1234L);
    currentVersion.setVersion(3);

    BulkImportUpdateContainer<SectorUpdateCsvModel> container =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid(currentVersion.getSloid())
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2023, 1, 1))
                .build())
            .attributesToNull(List.of(SectorUpdateCsvModel.Fields.length,
                SectorUpdateCsvModel.Fields.edgeHeight, SectorUpdateCsvModel.Fields.height))
            .build();

    CreateSectorVersionModel result = SectorBulkImportUpdate.apply(container, currentVersion);

    assertThat(result.getLength()).isNull();
    assertThat(result.getEdgeHeight()).isNull();
    assertThat(result.getSectorGeolocation().getHeight()).isNull();
    assertThat(result.getSectorGeolocation().getNorth()).isEqualTo(currentVersion.getNorth());
  }

}
