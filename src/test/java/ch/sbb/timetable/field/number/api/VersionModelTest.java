package ch.sbb.timetable.field.number.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.api.VersionModel.VersionModelBuilder;
import ch.sbb.timetable.field.number.enumaration.Status;
import java.time.LocalDate;
import java.util.Set;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class VersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void shouldHaveDateValidationExceptionWhenValidFromIsBefore1900_1_1() {
    //given
    VersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(1899, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
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
    VersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(2100, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        lineVersion);

    //then
    assertThat(constraintViolations).isNotEmpty();
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
                                                         .map(ConstraintViolation::getMessage)
                                                         .collect(Collectors.toList());
    assertThat(violationMessages).contains("ValidTo must be between 1.1.1900 and 31.12.2099");
  }

  private VersionModelBuilder versionModel() {
    return VersionModel.builder()
                       .ttfnid("ch:1:fpfnid:100000")
                       .name("FPFN Name")
                       .number("BEX")
                       .swissTimetableFieldNumber("b0.BEX")
                       .validFrom(LocalDate.of(2020, 12, 12))
                       .validTo(LocalDate.of(2099, 12, 12));
  }

}
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

  @Test
  void businessOrganisationShouldNotHaveMoreThan50Chars() {
    // Given
    StringBuilder businessOrganisation = new StringBuilder("test");
    while (businessOrganisation.length() < 50) {
      businessOrganisation.append("test");
    }
    VersionModel version = versionModel().businessOrganisation(businessOrganisation.toString()).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }

  @Test
  void businessOrganisationShouldNotBeNull() {
    // Given
    VersionModel version = versionModel().businessOrganisation(null).build();
    // When
    Set<ConstraintViolation<VersionModel>> constraintViolations = validator.validate(
        version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath())
        .hasToString("businessOrganisation");
  }

  private static VersionModelBuilder versionModel() {
    return VersionModel.builder()
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("a.90")
        .number("10.100")
        .validFrom(LocalDate.of(2021, 12, 1))
        .validTo(LocalDate.of(2022, 12, 1))
        .businessOrganisation("sbb");
  }
}
