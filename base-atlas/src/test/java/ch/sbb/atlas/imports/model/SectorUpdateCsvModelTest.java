package ch.sbb.atlas.imports.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SectorUpdateCsvModelTest {

  @Test
  void shouldBeValidSectorUpdateCsvModel() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:1:1:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("A")
        .east(2600037.945)
        .north(1199749.812)
        .spatialReference(SpatialReference.LV95)
        .height(540.2)
        .length(1.0)
        .edgeHeight(60.4)
        .build();
    assertThat(sector.validate()).isEmpty();
  }

  @Test
  void shouldBeValidMinimalSectorUpdateCsvModel() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:1:1:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(sector.validate()).isEmpty();
  }

  @Test
  void shouldReportMissingSloid() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(sector.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidFrom() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:1:1:1")
        .validTo(LocalDate.of(2099, 12, 31))
        .build();
    assertThat(sector.validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidTo() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:1:1:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .build();
    assertThat(sector.validate()).hasSize(1);
  }

  @Test
  void shouldReportAllMissingMandatoryFields() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder().build();
    assertThat(sector.validate()).hasSize(3);
  }

  @Test
  void shouldDeclareSloidAsUniqueField() {
    SectorUpdateCsvModel sector = SectorUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000:1:1:1")
        .build();
    assertThat(sector.uniqueFields()).hasSize(1).first()
        .extracting(uniqueField -> uniqueField.getFieldValueExtractor().apply(sector))
        .isEqualTo("ch:1:sloid:7000:1:1:1");
  }

}
