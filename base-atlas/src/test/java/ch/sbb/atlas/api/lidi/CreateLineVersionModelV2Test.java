package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class CreateLineVersionModelV2Test extends BaseValidatorTest {

  @ParameterizedTest
  @EnumSource(value = LineConcessionType.class, names = {"COLLECTION_LINE", "LINE_OF_A_TERRITORIAL_CONCESSION", "LINE_ABROAD",
      "FEDERAL_TERRITORIAL_CONCESSION", "CANTONALLY_APPROVED_LINE", "FEDERALLY_LICENSED_OR_APPROVED_LINE",
      "VARIANT_OF_A_FRANCHISED_LINE", "RACK_FREE_UNPUBLISHED_LINE", "RACK_FREE_TRIPS"})
  void shouldValidateConcessionTypeWithLineTypeOrderly(LineConcessionType concessionType) {
    //given
    CreateLineVersionModelV2 lineVersionModelV2 = CreateLineVersionModelV2.builder()
        .lineType(LineType.ORDERLY)
        .lineConcessionType(concessionType)
        .offerCategory(OfferCategory.IC)
        .shortNumber("6")
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation(
            "businessOrganisation")
        .comment("comment")
        .lineVersionWorkflows(Collections.emptySet())
        .swissLineNumber("swissLineNumber")
        .build();
    //when
    Set<ConstraintViolation<CreateLineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldValidateConcessionType(LineType lineType) {
    //given
    CreateLineVersionModelV2 lineVersionModelV2 = CreateLineVersionModelV2.builder()
        .lineType(lineType)
        .lineConcessionType(null)
        .offerCategory(OfferCategory.IC)
        .shortNumber("6")
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation(
            "businessOrganisation")
        .comment("comment")
        .lineVersionWorkflows(Collections.emptySet())
        .build();
    //when
    Set<ConstraintViolation<CreateLineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

  @Test
  void shouldValidateLineTypeOrderlyWithConcessionTypeAndSwissLineNumber() {
    //given
    CreateLineVersionModelV2 lineVersionModelV2 = CreateLineVersionModelV2.builder()
        .lineType(LineType.ORDERLY)
        .offerCategory(OfferCategory.IC)
        .lineConcessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .shortNumber("6")
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation(
            "businessOrganisation")
        .comment("comment")
        .lineVersionWorkflows(Collections.emptySet())
        .swissLineNumber("swissLineNumber")
        .build();
    //when
    Set<ConstraintViolation<CreateLineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

}