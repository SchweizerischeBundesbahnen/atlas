package ch.sbb.atlas.api.servicepoint.sector;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReadSectorGroupVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldProvideAdditionalInformationCorrectly() {
    ReadSectorVersionModel sectorVersionModel = ReadSectorVersionModel.builder()
        .trafficPointSloid("cde")
        .north(11.11)
        .east(11.22)
        .spatialReference(SpatialReference.LV95)
        .designation("Bern")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadSectorVersionModel sectorVersionModel2 = ReadSectorVersionModel.builder()
        .trafficPointSloid("cde")
        .north(11.11)
        .east(11.22)
        .spatialReference(SpatialReference.LV95)
        .designation("Bern")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadSectorGroupVersionModel readSectorGroupVersionModel = ReadSectorGroupVersionModel.builder()
        .sloid("abc")
        .trafficPointSloid("cde")
        .designation("hehe")
        .sectorVersions(List.of(sectorVersionModel, sectorVersionModel2))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<ReadSectorGroupVersionModel>> constraintViolations = validator.validate(readSectorGroupVersionModel);
    assertThat(constraintViolations).isEmpty();
  }
}
