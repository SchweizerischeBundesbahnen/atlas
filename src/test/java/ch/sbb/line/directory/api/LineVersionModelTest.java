package ch.sbb.line.directory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.api.LineVersionModel.LineVersionModelBuilder;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class LineVersionModelTest {

  private static final String THREE_HUNDRED_CHAR_STRING = "This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going to be long. This is going ";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 12, 12);
  private static final LocalDate VALID_TO = LocalDate.of(2099, 12, 12);
  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);

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
    return LineVersionModel.builder()
                           .status(Status.ACTIVE)
                           .type(LineType.ORDERLY)
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