package ch.sbb.timetable.field.number.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.api.VersionModel.VersionModelBuilder;
import ch.sbb.timetable.field.number.enumaration.Status;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class VersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldBuildValidVersion() {
    // Given
    VersionModel version = versionModel().build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel version = versionModel().comment(comment.toString()).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("comment");
  }

  @Test
  void swissTimetableFieldNumberShouldNotBeNull() {
    // Given
    VersionModel version = versionModel().swissTimetableFieldNumber(null).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel version = versionModel().swissTimetableFieldNumber(sttfn.toString()).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("swissTimetableFieldNumber");
  }

  @Test
  void numberShouldNotBeNull() {
    // Given
    VersionModel version = versionModel().number(null).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel version = versionModel().number(number.toString()).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(2);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("number");
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("number");
  }

  @Test
  void numberShouldMatchPattern() {
    // Given
    VersionModel version = versionModel().number("10?500").build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("number");
  }

  @Test
  void nameShouldNotHaveMoreThan255Chars() {
    // Given
    StringBuilder name = new StringBuilder("test");
    while (name.length() < 255) {
      name.append("test");
    }
    VersionModel version = versionModel().name(name.toString()).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("name");
  }

  private static VersionModelBuilder versionModel() {
    return VersionModel.builder()
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("a.90")
        .number("10.100");
  }
}
