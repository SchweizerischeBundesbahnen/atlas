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

class LineVersionModelV2Test extends BaseValidatorTest {

  @ParameterizedTest
  @EnumSource(value = LineConcessionType.class, names = {"COLLECTION_LINE", "LINE_OF_A_ZONE_CONCESSION", "LINE_ABROAD",
      "FEDERAL_ZONE_CONCESSION", "CANTONALLY_APPROVED_LINE", "FEDERALLY_LICENSED_OR_APPROVED_LINE",
      "VARIANT_OF_A_LICENSED_LINE", "NOT_LICENSED_UNPUBLISHED_LINE", "RIGHT_FREE_LINE"})
  void shouldValidateConcessionTypeWithLineTypeOrderly(LineConcessionType concessionType) {
    //given
    LineVersionModelV2 lineVersionModelV2 = LineVersionModelV2.builder()
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
    Set<ConstraintViolation<LineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "OPERATIONAL", "TEMPORARY"})
  void shouldValidateConcessionType(LineType lineType) {
    //given
    LineVersionModelV2 lineVersionModelV2 = LineVersionModelV2.builder()
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
    Set<ConstraintViolation<LineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

  @Test
  void shouldValidateLineTypeOrderlyWithConcessionTypeAndSwissLineNumber() {
    //given
    LineVersionModelV2 lineVersionModelV2 = LineVersionModelV2.builder()
        .lineType(LineType.ORDERLY)
        .offerCategory(OfferCategory.IC)
        .lineConcessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
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
    Set<ConstraintViolation<LineVersionModelV2>> constraintViolations = validator.validate(lineVersionModelV2);
    //then
    assertThat(constraintViolations).isEmpty();

  }

}