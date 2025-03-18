package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.SublineVersionModel.SublineVersionModelBuilder;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SublineVersionModelTest {

  private static final String THREE_HUNDRED_CHAR_STRING = "This is going to be long. This is going to be long. This is going to"
      + " be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This"
      + " is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going ";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 12, 12);
  private static final LocalDate VALID_TO = LocalDate.of(2099, 12, 12);

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static SublineVersionModelBuilder<?, ?> sublineVersionModel() {
    return SublineVersionModel.builder()
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid("mainlineSlnid");
  }

  @ParameterizedTest
  @EnumSource(value = SublineConcessionType.class, names = {"LINE_ABROAD",
      "CANTONALLY_APPROVED_LINE", "FEDERALLY_LICENSED_OR_APPROVED_LINE",
      "VARIANT_OF_A_LICENSED_LINE", "NOT_LICENSED_UNPUBLISHED_LINE", "RIGHT_FREE_LINE"})
  void shouldValidateConcessionTypeWithLineTypeOrderly(SublineConcessionType concessionType) {
    //given
    SublineVersionModelV2 sublineVersionModelV2 = SublineVersionModelV2.builder()
        .sublineConcessionType(concessionType)
        .longName("longName")
        .description("description")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid("mainlineSlnid")
        .slnid("ch:1:slind:1000:8")
        .swissSublineNumber("b1.L1.X")
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModelV2>> constraintViolations = validator.validate(sublineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

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
  void shouldHaveDescription() {
    // Given
    SublineVersionModel sublineVersion = sublineVersionModel().description("").build();
    // When
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(sublineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("description");
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
  void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validFrom(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidFromIsAfter9999_12_31() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validFrom(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "validTo must not be before validFrom",
        "ValidFrom must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsBefore1700_1_1() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validTo(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(2);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "validTo must not be before validFrom",
        "ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsAfter9999_12_31() {
    //given
    SublineVersionModel lineVersion = sublineVersionModel()
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<SublineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1700 and 31.12.9999");
  }
}
