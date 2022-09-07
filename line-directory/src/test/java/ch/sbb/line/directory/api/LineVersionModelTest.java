package ch.sbb.line.directory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.api.LineVersionModel.LineVersionModelBuilder;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class LineVersionModelTest {

  private static final String THREE_HUNDRED_CHAR_STRING = "This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going ";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 12, 12);

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldBuildValidLineVersion() {
    // Given
    LineVersionModel lineVersion = lineVersionModel().build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldHaveSwissLineNumber() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().swissLineNumber("").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(2);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "swissLineNumber");
  }

  @Test
  void shouldHaveBusinessOrganisation() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "businessOrganisation");
  }

  @Test
  void shouldHaveQuoVadisConformDescription() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().description(
        THREE_HUNDRED_CHAR_STRING).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "description");
  }

  @Test
  void shouldHaveValidFromBeforeValidTo() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().validTo(VALID_FROM.minusDays(1)).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validToEqualOrGreaterThenValidFrom");
  }

  @Test
  void shouldAllowOneDayValidLines() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().validTo(VALID_FROM).build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    LineVersionModel lineVersion = lineVersionModel()
        .validFrom(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsAfter2099_12_31() {
    //given
    LineVersionModel lineVersion = lineVersionModel()
        .validFrom(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

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
  public void shouldHaveDateValidationExceptionWhenValidToIsBefore1700_1_1() {
    //given
    LineVersionModel lineVersion = lineVersionModel()
        .validTo(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

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
  public void shouldHaveDateValidationExceptionWhenValidToIsAfter9999_12_31() {
    //given
    LineVersionModel lineVersion = lineVersionModel()
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        lineVersion);

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
    LineVersionModel LineVersion = lineVersionModel().colorFontRgb("FFFFFF").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "colorFontRgb");
  }

  @Test
  void shouldFailOnInvalidCmykColor() {
    // Given
    LineVersionModel LineVersion = lineVersionModel().colorFontCmyk("101,0,1,100").build();
    // When
    Set<ConstraintViolation<LineVersionModel>> constraintViolations = validator.validate(
        LineVersion);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "colorFontCmyk");
  }

  private static LineVersionModelBuilder lineVersionModel() {
    return LineTestData.lineVersionModelBuilder()
                       .status(Status.ACTIVE)
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
}