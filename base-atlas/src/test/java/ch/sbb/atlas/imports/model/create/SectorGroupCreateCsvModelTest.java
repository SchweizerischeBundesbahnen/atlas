package ch.sbb.atlas.imports.model.create;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorGroupCreateCsvModelTest {

  private static final Set<String> TWO_SECTOR_SLOIDS = Set.of("ch:1:sloid:7000:1:1:1", "ch:1:sloid:7000:1:1:2");

  private static SectorGroupCreateCsvModel.SectorGroupCreateCsvModelBuilder validModel() {
    return SectorGroupCreateCsvModel.builder()
        .trafficPointSloid("ch:1:sloid:7000:1:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("AB")
        .length(35.0)
        .sectorSloids(TWO_SECTOR_SLOIDS);
  }

  @Test
  void shouldBeValidSectorGroupCreateCsvModel() {
    assertThat(validModel().build().validate()).isEmpty();
  }

  @Test
  void shouldBeValidMinimalSectorGroupCreateCsvModel() {
    SectorGroupCreateCsvModel sectorGroup = validModel().length(null).build();
    assertThat(sectorGroup.validate()).isEmpty();
  }

  @Test
  void shouldReportMissingTrafficPointSloid() {
    assertThat(validModel().trafficPointSloid(null).build().validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidFrom() {
    assertThat(validModel().validFrom(null).build().validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingValidTo() {
    assertThat(validModel().validTo(null).build().validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingDesignation() {
    assertThat(validModel().designation(null).build().validate()).hasSize(1);
  }

  @Test
  void shouldReportMissingSectorSloids() {
    assertThat(validModel().sectorSloids(null).build().validate()).hasSize(1);
  }

  @Test
  void shouldReportTooFewSectorSloids() {
    SectorGroupCreateCsvModel sectorGroup = validModel().sectorSloids(Set.of("ch:1:sloid:7000:1:1:1")).build();

    assertThat(sectorGroup.validate()).hasSize(1).first()
        .extracting(error -> error.getDisplayInfo().getCode())
        .isEqualTo("BULK_IMPORT.VALIDATION.FIELD_MIN_SIZE");
  }

  @Test
  void shouldNotReportTooFewSectorSloidsWhenSectorSloidsAreMissing() {
    SectorGroupCreateCsvModel sectorGroup = validModel().sectorSloids(null).build();

    assertThat(sectorGroup.validate()).hasSize(1).first()
        .extracting(error -> error.getDisplayInfo().getCode())
        .isEqualTo("BULK_IMPORT.VALIDATION.FIELD_MANDATORY");
  }

  @Test
  void shouldHaveNoUniqueFields() {
    assertThat(validModel().build().uniqueFields()).isEqualTo(List.of());
  }

}
