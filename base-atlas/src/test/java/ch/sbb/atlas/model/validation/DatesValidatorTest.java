package ch.sbb.atlas.model.validation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.validation.DatesValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class DatesValidatorTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldMakeSureFromIsBeforeTo() {
    // Given
    DummyDatesValidator object = DummyDatesValidator.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<DummyDatesValidator>> constraintViolations = validator.validate(
        object);

    //then
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("validTo must not be before validFrom");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    DummyDatesValidator dummyDatesValidator = DummyDatesValidator.builder()
        .validFrom(
            LocalDate.of(1699, 12, 31))
        .validTo(
            LocalDate.of(2000, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<DummyDatesValidator>> constraintViolations = validator.validate(
        dummyDatesValidator);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidFromIsAfter2099_12_31() {
    //given
    DummyDatesValidator dummyDatesValidator = DummyDatesValidator.builder()
        .validTo(
            LocalDate.of(2000, 12, 31))
        .validFrom(
            LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<DummyDatesValidator>> constraintViolations = validator.validate(
        dummyDatesValidator);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("validTo must not be before validFrom",
        "ValidFrom must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsBefore1700_1_1() {
    //given
    DummyDatesValidator dummyDatesValidator = DummyDatesValidator.builder()
        .validFrom(
            LocalDate.of(2000, 12, 31))
        .validTo(
            LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<DummyDatesValidator>> constraintViolations = validator.validate(
        dummyDatesValidator);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("validTo must not be before validFrom",
        "ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsAfter9999_12_31() {
    //given
    DummyDatesValidator dummyDatesValidator = DummyDatesValidator.builder()
        .validFrom(
            LocalDate.of(2000, 12, 31))
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<DummyDatesValidator>> constraintViolations = validator.validate(
        dummyDatesValidator);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @RequiredArgsConstructor
  @Builder
  @Getter
  private static class DummyDatesValidator implements DatesValidator {

    private final LocalDate validFrom;
    private final LocalDate validTo;

  }
}
