package ch.sbb.line.directory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.api.SublineVersionModel.SublineVersionModelBuilder;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class SublineVersionModelTest {

  private static final String THREE_HUNDRED_CHAR_STRING = "This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going ";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 12, 12);
  private static final LocalDate VALID_TO = LocalDate.of(2099, 12, 12);

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldBuildValidSublineVersion() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        sublineVersion);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldHaveSwissSublineNumber() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().swissSublineNumber("").build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        sublineVersion);

    // Then
    assertThat(constraintViolations).hasSize(2);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "swissSublineNumber");
  }

  @Test
  void shouldHaveBusinessOrganisation() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        sublineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "businessOrganisation");
  }

  @Test
  void shouldHaveQuoVadisConformDescription() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().description(
        THREE_HUNDRED_CHAR_STRING).build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        sublineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "description");
  }

  @Test
  void shouldHaveValidFromBeforeValidTo() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().validTo(VALID_FROM.minusDays(1)).build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        sublineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validToEqualOrGreaterThenValidFrom");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsBefore1900_1_1() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validFrom(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).isNotEmpty();
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsAfter2099_12_31() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validFrom(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).isNotEmpty();
    assertThat(constraintViolations).hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
                                                         .map(ConstraintViolation::getMessage)
                                                         .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "validTo must not be before validFrom",
        "ValidFrom must be between 1.1.1900 and 31.12.2099");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidToIsBefore1900_1_1() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validTo(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).isNotEmpty();
    assertThat(constraintViolations).hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
                                                         .map(ConstraintViolation::getMessage)
                                                         .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "validTo must not be before validFrom",
        "ValidTo must be between 1.1.1900 and 31.12.2099");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidToIsAfter2099_12_31() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validTo(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).isNotEmpty();
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
                                                         .map(ConstraintViolation::getMessage)
                                                         .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1900 and 31.12.2099");
  }

  private static SublineVersionModelBuilder sublineVersionModel() {
    return SublineVersionModel.builder()
                              .status(Status.ACTIVE)
                              .sublineType(SublineType.TECHNICAL)
                              .paymentType(PaymentType.INTERNATIONAL)
                              .number("number")
                              .longName("longName")
                              .description("description")
                              .validFrom(VALID_FROM)
                              .validTo(VALID_TO)
                              .businessOrganisation("businessOrganisation")
                              .mainlineSlnid("mainlineSlnid")
                              .swissSublineNumber("swissSublineNumber");
  }
}