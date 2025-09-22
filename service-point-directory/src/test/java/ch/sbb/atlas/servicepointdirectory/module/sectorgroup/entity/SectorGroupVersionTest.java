package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorGroupVersionTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldNotAcceptSectorGroupWithoutRequiredTrafficPointSloid() {
    // Given
    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("abc")
        .designation("off")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<SectorGroupVersion>> constraintViolations = validator.validate(sectorGroupVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAcceptSectorGroup() {
    // Given
    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("abc")
        .trafficPointSloid("traffic")
        .designation("off")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<SectorGroupVersion>> constraintViolations = validator.validate(sectorGroupVersion);

    //then
    assertThat(constraintViolations).hasSize(0);
  }

}
