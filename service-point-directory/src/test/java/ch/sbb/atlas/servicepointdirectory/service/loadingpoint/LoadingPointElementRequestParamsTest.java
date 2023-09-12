package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.service.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LoadingPointElementRequestParamsTest extends BaseValidatorTest {

  @Test
  void shouldNotAcceptWhenNumberLessThan1000000(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().numbers(List.of(123))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isNotEmpty();
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be greater than or equal to 1000000");

  }
  @Test
  void shouldNotAcceptWhenNumberBiggerThan9999999(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().numbers(List.of(
            10000000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isNotEmpty();
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be less than or equal to 9999999");

  }

  @Test
  void shouldAcceptNumberWith7Digits(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().numbers(List.of(
            8507000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldNotAcceptWhenServicePointNumberLessThan1000000(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().servicePointNumbers(List.of(123))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isNotEmpty();
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be greater than or equal to 1000000");

  }
  @Test
  void shouldNotAcceptWhenServicePointNumberBiggerThan9999999(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().servicePointNumbers(List.of(
            10000000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isNotEmpty();
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be less than or equal to 9999999");

  }

  @Test
  void shouldAcceptServicePointNumberWith7Digits(){
    //given
    LoadingPointElementRequestParams requestParams = LoadingPointElementRequestParams.builder().servicePointNumbers(List.of(
            8507000))
        .build();
    //when
    Set<ConstraintViolation<LoadingPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isNotNull();
    assertThat(violations).isEmpty();
  }

}