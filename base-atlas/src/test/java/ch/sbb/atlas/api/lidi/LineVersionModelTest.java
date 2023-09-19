package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModel.LineVersionModelBuilder;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.model.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class LineVersionModelTest {

  private static final String THREE_HUNDRED_CHAR_STRING = "This is going to be long. This is going to be long. This is going to"
      + " be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This"
      + " is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going ";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 12, 12);

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static LineVersionModelBuilder<?, ?> lineVersionModel() {
    return LineVersionModel.builder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .slnid("slnid")
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .alternativeName("alternativeName")
        .combinationName("combinationName")
        .longName("longName")
        .colorFontRgb("#FFFFFF")
        .colorBackRgb("#FFFFFF")
        .colorFontCmyk("10,0,100,7")
        .colorBackCmyk("10,0,100,7")
        .description("description")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .swissLineNumber("swissLineNumber");
  }

  @Test
  void shouldBuildValidLineVersion() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldHaveSwissLineNumber() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().swissLineNumber("").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(2);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "swissLineNumber");
  }

  @Test
  void shouldHaveBusinessOrganisation() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "businessOrganisation");
  }

  @Test
  void shouldHaveQuoVadisConformDescription() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().description(
        THREE_HUNDRED_CHAR_STRING).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "description");
  }

  @Test
  void shouldHaveValidFromBeforeValidTo() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().validTo(VALID_FROM.minusDays(1)).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validToEqualOrGreaterThenValidFrom");
  }

  @Test
  void shouldAllowOneDayValidLines() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().validTo(VALID_FROM).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
   void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    LineVersionModel lineVersionModel = lineVersionModel()
        .validFrom(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
   void shouldHaveDateValidationExceptionWhenValidFromIsAfter2099_12_31() {
    //given
    LineVersionModel lineVersionModel = lineVersionModel()
        .validFrom(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(2);
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
    LineVersionModel lineVersionModel = lineVersionModel()
        .validTo(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(2);
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
    LineVersionModel lineVersionModel = lineVersionModel()
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    //then
    assertThat(constraintViolations).isNotEmpty().hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldFailOnInvalidRgbColor() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().colorFontRgb("FFFFFF").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "colorFontRgb");
  }

  @Test
  void shouldFailOnInvalidCmykColor() {
    // Given
    LineVersionModel lineVersionModel = lineVersionModel().colorFontCmyk("101,0,1,100").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersionModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "colorFontCmyk");
  }
}
