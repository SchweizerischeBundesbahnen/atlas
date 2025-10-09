package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel.TimetableFieldNumberVersionModelBuilder;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

class TimetableFieldNumberVersionModelTest {

  private final Validator validator;

  private static TimetableFieldNumberVersionModelBuilder<?, ?> versionModel() {
    return TimetableFieldNumberVersionModel.builder()
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("a.90")
        .number("10.100")
        .validFrom(LocalDate.of(2021, 12, 1))
        .validTo(LocalDate.of(2022, 12, 1))
        .businessOrganisation("sbb")
        .descriptionOutwardLine1("test")
        .descriptionReturnLine1("test")
        .meanOfTransport(MeanOfTransport.TRAIN);
  }

  TimetableFieldNumberVersionModelTest() {
    try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
      validator = vf.getValidator();
    }
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidFromIsBefore1700_1_1() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(1699, 12, 31))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(lineVersion);

    //then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        "validFromValid");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidFromIsAfter9999_12_31() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validFrom(LocalDate.of(10000, 1, 1))
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
        "ValidFrom must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsBefore1700_1_1() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(1699, 12, 31))
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
        "ValidTo must be between 1.1.1700 and 31.12.9999");
  }

  @Test
  void shouldHaveDateValidationExceptionWhenValidToIsAfter9999_12_31() {
    //given
    TimetableFieldNumberVersionModel lineVersion = versionModel()
        .validTo(LocalDate.of(10000, 1, 1))
        .build();
    //when
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(
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
    TimetableFieldNumberVersionModel version = versionModel().build();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(version);
    // Then
    assertThat(constraintViolations).isEmpty();
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

  static Supplier<Stream<Arguments>> descriptionSupplier = () -> Stream.of(
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionOutwardLine1(s).build(),
          "descriptionOutwardLine1"),
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionOutwardLine2(s).build(),
          "descriptionOutwardLine2"),
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionOutwardLine3(s).build(),
          "descriptionOutwardLine3"),
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionReturnLine1(s).build(),
          "descriptionReturnLine1"),
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionReturnLine2(s).build(),
          "descriptionReturnLine2"),
      Arguments.of((Function<String, TimetableFieldNumberVersionModel>) s -> versionModel().descriptionReturnLine3(s).build(),
          "descriptionReturnLine3")
  );

  @ParameterizedTest
  @FieldSource("descriptionSupplier")
  void descriptionShouldNotHaveMoreThan255Chars(Function<String, TimetableFieldNumberVersionModel> modelSupplier,
      String fieldName) {
    // Given
    StringBuilder description = new StringBuilder("test");
    while (description.length() < 255) {
      description.append("test");
    }
    TimetableFieldNumberVersionModel version = modelSupplier.apply(description.toString());
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(version);
    // Then
    long numberOfFieldViolations = constraintViolations.stream()
        .filter(v -> String.valueOf(v.getPropertyPath()).equals(fieldName))
        .count();
    assertThat(numberOfFieldViolations).isEqualTo(1);
  }

  @ParameterizedTest
  @FieldSource("descriptionSupplier")
  void descriptionShouldOnlyAllowISO(Function<String, TimetableFieldNumberVersionModel> modelSupplier, String fieldName) {
    // Given
    TimetableFieldNumberVersionModel version = modelSupplier.apply("≠");
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(version);
    // Then
    long numberOfFieldViolations = constraintViolations.stream()
        .filter(v -> String.valueOf(v.getPropertyPath()).equals(fieldName))
        .count();
    assertThat(numberOfFieldViolations).isEqualTo(1);
  }

  static Supplier<Stream<Arguments>> fieldsShouldNotAllowNull = () -> Stream.of(
      Arguments.of((Supplier<TimetableFieldNumberVersionModel>) () -> versionModel().descriptionOutwardLine1(null).build(),
          "descriptionOutwardLine1"),
      Arguments.of((Supplier<TimetableFieldNumberVersionModel>) () -> versionModel().descriptionReturnLine1(null).build(),
          "descriptionReturnLine1"),
      Arguments.of((Supplier<TimetableFieldNumberVersionModel>) () -> versionModel().meanOfTransport(null).build(),
          "meanOfTransport")
  );

  @ParameterizedTest
  @FieldSource
  void fieldsShouldNotAllowNull(Supplier<TimetableFieldNumberVersionModel> modelSupplier, String fieldName) {
    // Given
    TimetableFieldNumberVersionModel version = modelSupplier.get();
    // When
    Set<ConstraintViolation<TimetableFieldNumberVersionModel>> constraintViolations = validator.validate(version);
    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(fieldName);
  }

  // todo: test that @ValidTtfnDescription validation is triggered on endpoint hit
  //  and test validator logic isolated

  @Test
  void businessOrganisationShouldNotHaveMoreThan50Chars() {
    // Given
    StringBuilder businessOrganisation = new StringBuilder("test");
    while (businessOrganisation.length() < 50) {
      businessOrganisation.append("test");
    }
    TimetableFieldNumberVersionModel version = versionModel().businessOrganisation(businessOrganisation.toString())
        .build();
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
}
