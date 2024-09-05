package ch.sbb.atlas.configuration.handler;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;
import org.junit.jupiter.api.Test;

class ConstraintViolationMapperTest {

  @Test
  void shouldMapToErrorResponse() {
    ConstraintViolationMapper constraintViolationMapper = new ConstraintViolationMapper(
        getExampleConstraintViolationDesignationOfficialSize().getConstraintViolations());

    SortedSet<Detail> actual = constraintViolationMapper.getDetails();

    assertThat(actual).hasSize(1);
    Detail detail = actual.first();
    assertThat(detail.getField()).isEqualTo("designationOfficial");
    assertThat(detail.getDisplayInfo().getCode()).isEqualTo("ERROR.CONSTRAINT.SIZE");

    List<Parameter> neededDetails = List.of(
        new Parameter("propertyPath", "designationOfficial"),
        new Parameter("value", "BernZuLangBernZuLangBernZuLangBernZuLangBernZuLang"),
        new Parameter("min", "2"),
        new Parameter("max", "30"),
        new Parameter("message", "{jakarta.validation.constraints.Size.message}"));
    assertThat(detail.getDisplayInfo().getParameters()).containsExactlyInAnyOrderElementsOf(neededDetails);
  }

  static ConstraintViolationException getExampleConstraintViolationDesignationOfficialSize() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("BernZuLangBernZuLangBernZuLangBernZuLangBernZuLang")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    return new ConstraintViolationException(validator.validate(servicePointVersionModel));
  }

}