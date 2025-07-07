package ch.sbb.atlas.api.servicepoint.sector;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UpdateSectorGroupVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowUpdateSectorGroupVersion() {
    UpdateSectorGroupVersionModel updateSectorGroupVersionModel = UpdateSectorGroupVersionModel.builder()
        .designation("Bern")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateSectorGroupVersionModel>> constraintViolations = validator.validate(
        updateSectorGroupVersionModel);
    assertThat(constraintViolations).isEmpty();

  }
  
}
