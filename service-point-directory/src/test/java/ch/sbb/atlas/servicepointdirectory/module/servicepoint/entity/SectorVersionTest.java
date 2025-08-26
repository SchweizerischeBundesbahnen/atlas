package ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorVersionTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldNotAcceptSectorWithoutRequiredTrafficPointSloid() {
    // Given
    SectorVersion sectorVersion = SectorVersion.builder()
        .sloid("abc")
        .north(111.111)
        .east(22.222)
        .spatialReference(SpatialReference.LV95)
        .designation("off")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .status(Status.VALIDATED)
        .build();
    //when
    Set<ConstraintViolation<SectorVersion>> constraintViolations = validator.validate(sectorVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptSectorWithoutGeoInformation() {
    // Given
    SectorVersion sectorVersion = SectorVersion.builder()
        .sloid("abc")
        .trafficPointSloid("traffic")
        .designation("off")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .status(Status.VALIDATED)
        .build();
    //when
    Set<ConstraintViolation<SectorVersion>> constraintViolations = validator.validate(sectorVersion);

    //then
    assertThat(constraintViolations).hasSize(3);
  }

}
