package ch.sbb.line.directory.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.api.TimetableFieldNumberVersionModel.TimetableFieldNumberVersionModelBuilder;
import ch.sbb.line.directory.enumaration.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class TimetableFieldNumberVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsBefore1900_1_1() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsAfter2099_12_31() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
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
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
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
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1900 and 31.12.2099");
  }

  @Test
  void shouldBuildValidVersion() {
    // Given
    TimetableFieldNumberVersionModel version = versionModel().build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void commentShouldNotHaveMoreThan250Chars() {
    // Given
    StringBuilder comment = new StringBuilder("test");
    while (comment.length() < 250) {
      comment.append("test");
    }
    TimetableFieldNumberVersionModel version = versionModel().comment(comment.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("comment");
  }

  @Test
  void swissTimetableFieldNumberShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionModel version = versionModel().swissTimetableFieldNumber(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("swissTimetableFieldNumber");
  }

  @Test
  void swissTimetableFieldNumberShouldNotHaveMoreThan50Chars() {
    // Given
    StringBuilder sttfn = new StringBuilder("test");
    while (sttfn.length() < 50) {
      sttfn.append("test");
    }
    TimetableFieldNumberVersionModel version = versionModel().swissTimetableFieldNumber(sttfn.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("swissTimetableFieldNumber");
  }

  @Test
  void numberShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionModel version = versionModel().number(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("number");
  }

  @Test
  void numberShouldNotHaveMoreThan50Chars() {
    // Given
    StringBuilder number = new StringBuilder("10.");
    while (number.length() < 50) {
      number.append("10");
    }
    TimetableFieldNumberVersionModel version = versionModel().number(number.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("number");
  }

  @Test
  void numberShouldMatchPattern() {
    // Given
    TimetableFieldNumberVersionModel version = versionModel().number("10?500").build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("number");
  }

  @Test
  void descriptionShouldNotHaveMoreThan255Chars() {
    // Given
    StringBuilder description = new StringBuilder("test");
    while (description.length() < 255) {
      description.append("test");
    }
    TimetableFieldNumberVersionModel version = versionModel().description(description.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("description");
  }

  @Test
  void businessOrganisationShouldNotHaveMoreThan50Chars() {
    // Given
    StringBuilder businessOrganisation = new StringBuilder("test");
    while (businessOrganisation.length() < 50) {
      businessOrganisation.append("test");
    }
    TimetableFieldNumberVersionModel version = versionModel().businessOrganisation(businessOrganisation.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }

  @Test
  void businessOrganisationShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionModel version = versionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }

  private static TimetableFieldNumberVersionModelBuilder versionModel() {
    return TimetableFieldNumberVersionModel.builder()
                                           .status(Status.ACTIVE)
                                           .swissTimetableFieldNumber("a.90")
                                           .number("10.100")
                                           .validFrom(LocalDate.of(2021, 12, 1))
                                           .validTo(LocalDate.of(2022, 12, 1))
                                           .businessOrganisation("sbb");
  }
}
