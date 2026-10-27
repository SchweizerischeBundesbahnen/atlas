package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateLineVersionModelV3Test extends BaseValidatorTest {

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> valid() {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .description("description")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31));
  }

  private Set<String> properties(CreateLineVersionModelV3 model) {
    Set<ConstraintViolation<CreateLineVersionModelV3>> violations = validator.validate(model);
    return violations.stream().map(v -> v.getPropertyPath().toString()).collect(java.util.stream.Collectors.toSet());
  }

  @Test
  void shouldAcceptValidLine() {
    // When
    Set<ConstraintViolation<CreateLineVersionModelV3>> violations = validator.validate(valid().build());

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldAcceptOptionalReferencedLineSlnid() {
    // When
    Set<ConstraintViolation<CreateLineVersionModelV3>> violations =
        validator.validate(valid().referencedLineSlnid("ch:1:slnid:1000001").build());

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRequireLineType() {
    // When / Then
    assertThat(properties(valid().lineType(null).build())).containsExactly("lineType");
  }

  @Test
  void shouldRequireDescription() {
    // When / Then
    assertThat(properties(valid().description(null).build())).containsExactly("description");
  }

  @Test
  void shouldRequireBusinessOrganisation() {
    // When / Then
    assertThat(properties(valid().businessOrganisation(null).build())).contains("businessOrganisation");
  }

  @Test
  void shouldRequireValidityDates() {
    // When / Then
    assertThat(properties(valid().validFrom(null).validTo(null).build())).contains("validFrom", "validTo");
  }

  @Test
  void shouldRejectValidToBeforeValidFrom() {
    // When
    Set<ConstraintViolation<CreateLineVersionModelV3>> violations =
        validator.validate(valid().validTo(LocalDate.of(2019, 1, 1)).build());

    // Then
    assertThat(violations).extracting(ConstraintViolation::getMessage).contains("validTo must not be before validFrom");
  }

  @Test
  void shouldRejectTooLongDescription() {
    // When / Then
    assertThat(properties(valid().description("a".repeat(256)).build())).containsExactly("description");
  }

  @Test
  void shouldRejectTooLongCablewayNumber() {
    // When / Then
    assertThat(properties(valid().cablewayNumber("a".repeat(51)).build())).containsExactly("cablewayNumber");
  }

  @Test
  void shouldRejectSlnidStatusAndEtagVersion() {
    // Given
    CreateLineVersionModelV3 model = valid().slnid("ch:1:slnid:1").etagVersion(0).build();

    // When / Then
    assertThat(properties(model)).containsExactlyInAnyOrder("slnid", "etagVersion");
  }
}
