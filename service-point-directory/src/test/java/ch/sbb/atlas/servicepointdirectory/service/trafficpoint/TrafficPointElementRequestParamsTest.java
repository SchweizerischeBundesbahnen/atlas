package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrafficPointElementRequestParamsTest extends BaseValidatorTest {

  @Test
  void shouldNotAcceptWhenServicePointNumberLessThan1000000(){
    //given
    TrafficPointElementRequestParams requestParams = TrafficPointElementRequestParams.builder().servicePointNumbers(List.of("123"))
        .build();
    //when
    Set<ConstraintViolation<TrafficPointElementRequestParams>> violations = validator.validate(requestParams);

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
    TrafficPointElementRequestParams requestParams = TrafficPointElementRequestParams.builder().servicePointNumbers(List.of(
        "10000000"))
        .build();
    //when
    Set<ConstraintViolation<TrafficPointElementRequestParams>> violations = validator.validate(requestParams);

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
    TrafficPointElementRequestParams requestParams = TrafficPointElementRequestParams.builder().servicePointNumbers(List.of(
        "8507000"))
        .build();
    //when
    Set<ConstraintViolation<TrafficPointElementRequestParams>> violations = validator.validate(requestParams);

    //then
    assertThat(violations).isEmpty();
  }

}