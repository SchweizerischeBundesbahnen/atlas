package ch.sbb.atlas.api.servicepoint.sector;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UpdateSectorVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowUpdateSectorVersion() {
    UpdateSectorVersionModel updateSectorVersionModel = UpdateSectorVersionModel.builder()
        .designation("Bern")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .north(22.22)
        .east(11.11)
        .spatialReference(SpatialReference.LV95)
        .build();

    Set<ConstraintViolation<UpdateSectorVersionModel>> constraintViolations = validator.validate(updateSectorVersionModel);
    assertThat(constraintViolations).isEmpty();

  }

}
