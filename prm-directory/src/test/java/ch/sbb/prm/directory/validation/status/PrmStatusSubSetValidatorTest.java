package ch.sbb.prm.directory.validation.status;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.atlas.model.Status;
import ch.sbb.prm.directory.entity.BasePrmImportEntity;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PrmStatusSubSetValidatorTest extends BaseValidatorTest {


  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"DRAFT","IN_REVIEW","WITHDRAWN"})
  void shouldNotBeValid(Status status) {
    //given
    MyStatusObj myStatusObj = MyStatusObj.builder().version(0).status(status).build();
    //when
    Set<ConstraintViolation<MyStatusObj>> violations = validator.validate(myStatusObj);
    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be any of [VALIDATED, REVOKED]");

  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"REVOKED","VALIDATED"})
  void shouldBeValid(Status status) {
    //given
    MyStatusObj myStatusObj = MyStatusObj.builder().version(0).status(status).build();
    //when
    Set<ConstraintViolation<MyStatusObj>> violations = validator.validate(myStatusObj);
    //then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldNotBeValidWithNull() {
    //given
    MyStatusObj myStatusObj = MyStatusObj.builder().version(0).build();
    //when
    Set<ConstraintViolation<MyStatusObj>> violations = validator.validate(myStatusObj);
    //then
    assertThat(violations).hasSize(1);
    List<String> violationMessages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();
    assertThat(violationMessages).hasSize(1);
    assertThat(violationMessages.get(0)).isEqualTo("must be any of [VALIDATED, REVOKED]");
  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  @SuperBuilder
  static class MyStatusObj extends BasePrmImportEntity {

  }

}