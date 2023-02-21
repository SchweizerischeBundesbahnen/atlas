package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionVersionModel.TimetableFieldNumberVersionVersionModelBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class TimetableFieldNumberVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static TimetableFieldNumberVersionVersionModelBuilder<?, ?> versionModel() {
    return TimetableFieldNumberVersionVersionModel.builder()
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("a.90")
        .number("10.100")
        .validFrom(LocalDate.of(2021, 12, 1))
        .validTo(LocalDate.of(2022, 12, 1))
        .businessOrganisation("sbb");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    TimetableFieldNumberVersionVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsAfter9999_12_31() {
    //given
    TimetableFieldNumberVersionVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
  public void shouldHaveDateValidationExceptionWhenValidToIsBefore1700_1_1() {
    //given
    TimetableFieldNumberVersionVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
  public void shouldHaveDateValidationExceptionWhenValidToIsAfter9999_12_31() {
    //given
    TimetableFieldNumberVersionVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldBuildValidVersion() {
    // Given
    TimetableFieldNumberVersionVersionModel version = versionModel().build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void commentShouldNotHaveMoreThan1500Chars() {
    // Given
    StringBuilder comment = new StringBuilder("test");
    while (comment.length() <= AtlasFieldLengths.LENGTH_1500) {
      comment.append("test");
    }
    TimetableFieldNumberVersionVersionModel version = versionModel().comment(comment.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("comment");
  }

  @Test
  void swissTimetableFieldNumberShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionVersionModel version = versionModel().swissTimetableFieldNumber(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
    TimetableFieldNumberVersionVersionModel version = versionModel().swissTimetableFieldNumber(sttfn.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("swissTimetableFieldNumber");
  }

  @Test
  void numberShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionVersionModel version = versionModel().number(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
    TimetableFieldNumberVersionVersionModel version = versionModel().number(number.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("number");
  }

  @Test
  void numberShouldMatchPattern() {
    // Given
    TimetableFieldNumberVersionVersionModel version = versionModel().number("10?500").build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
    TimetableFieldNumberVersionVersionModel version = versionModel().description(description.toString()).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
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
    TimetableFieldNumberVersionVersionModel version = versionModel().businessOrganisation(businessOrganisation.toString())
        .build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }

  @Test
  void businessOrganisationShouldNotBeNull() {
    // Given
    TimetableFieldNumberVersionVersionModel version = versionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionVersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }
}
