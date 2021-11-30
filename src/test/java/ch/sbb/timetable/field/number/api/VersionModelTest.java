package ch.sbb.timetable.field.number.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.api.VersionModel.VersionModelBuilder;
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