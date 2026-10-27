package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UpdateLineVersionModelV3Test extends BaseValidatorTest {

  private static UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> valid() {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(1)
        .description("description")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31));
  }

  @Test
  void shouldAcceptValidUpdate() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations = validator.validate(valid().build());

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRequireEtagVersion() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations = validator.validate(valid().etagVersion(null).build());

    // Then
    assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsExactly("etagVersion");
  }

  @Test
  void shouldRequireDescriptionAndBusinessOrganisation() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations =
        validator.validate(valid().description(null).businessOrganisation(null).build());

    // Then
    assertThat(violations).extracting(v -> v.getPropertyPath().toString())
        .contains("description", "businessOrganisation");
  }

  @Test
  void shouldRejectValidToBeforeValidFrom() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations =
        validator.validate(valid().validTo(LocalDate.of(2019, 1, 1)).build());

    // Then
    assertThat(violations).extracting(ConstraintViolation::getMessage).contains("validTo must not be before validFrom");
  }

  @Test
  void shouldRejectLineTypeWithImmutabilityMessage() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations =
        validator.validate(valid().lineType(LineType.ORDERLY).build());

    // Then
    assertThat(violations).extracting(ConstraintViolation::getMessage)
        .containsExactly("lineType is immutable and must not be sent on update");
  }

  @Test
  void shouldRejectReferencedLineSlnidWithImmutabilityMessage() {
    // When
    Set<ConstraintViolation<UpdateLineVersionModelV3>> violations =
        validator.validate(valid().referencedLineSlnid("ch:1:slnid:1000001").build());

    // Then
    assertThat(violations).extracting(ConstraintViolation::getMessage)
        .containsExactly("referencedLineSlnid is immutable and must not be sent on update");
  }
}
