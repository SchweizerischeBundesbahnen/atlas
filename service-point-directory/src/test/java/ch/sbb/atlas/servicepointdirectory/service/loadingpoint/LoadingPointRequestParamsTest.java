package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LoadingPointRequestParamsTest extends BaseValidatorTest {

  @Test
  void shouldNotAcceptWhenNumberLessThan0(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().numbers(List.of(-1))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be greater than or equal to 0");

  }
  @Test
  void shouldNotAcceptWhenNumberBiggerThan9999999(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().numbers(List.of(
            10000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be less than or equal to 9999");

  }

  @Test
  void shouldAcceptNumberWith7Digits(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().numbers(List.of(
            8507))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldNotAcceptWhenServicePointNumberLessThan1000000(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().servicePointNumbers(List.of(123))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be greater than or equal to 1000000");

  }
  @Test
  void shouldNotAcceptWhenServicePointNumberBiggerThan9999999(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().servicePointNumbers(List.of(
            10000000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be less than or equal to 9999999");

  }

  @Test
  void shouldAcceptServicePointNumberWith7Digits(){
    //given
    LoadingPointRequestParams requestParams = LoadingPointRequestParams.builder().servicePointNumbers(List.of(
            8507000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isEmpty();
  }

}