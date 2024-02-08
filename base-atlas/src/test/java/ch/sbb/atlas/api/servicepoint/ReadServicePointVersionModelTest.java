package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReadServicePointVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldProvideAdditionalInformationCorrectly() {
    ReadServicePointVersionModel servicePointVersionModel = ReadServicePointVersionModel.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<ReadServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();

    assertThat(servicePointVersionModel.isStopPoint()).isFalse();
    assertThat(servicePointVersionModel.isTrafficPoint()).isFalse();
    assertThat(servicePointVersionModel.isFareStop()).isFalse();
    assertThat(servicePointVersionModel.isBorderPoint()).isFalse();
  }
}
