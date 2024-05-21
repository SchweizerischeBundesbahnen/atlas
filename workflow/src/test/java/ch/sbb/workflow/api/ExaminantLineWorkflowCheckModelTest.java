package ch.sbb.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.PersonModel;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class ExaminantWorkflowCheckModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static final PersonModel EXAMINANT = PersonModel.builder()
                                                          .firstName("Marek")
                                                          .lastName("Hamsik")
                                                          .personFunction("Centrocampista")
                                                          .build();

  @Test
  void shouldAcceptBavCheckWithoutCommentOnAccepted() {
    // Given
    ExaminantWorkflowCheckModel object = ExaminantWorkflowCheckModel.builder()
                                                                    .accepted(true)
                                                                    .examinant(EXAMINANT)
                                                                    .build();
    //when
    Set<ConstraintViolation<ExaminantWorkflowCheckModel>> constraintViolations = validator.validate(
        object);

    //then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptBavCheckWithoutCommentOnRejection() {
    // Given
    ExaminantWorkflowCheckModel object = ExaminantWorkflowCheckModel.builder()
                                                                    .accepted(false)
                                                                    .examinant(EXAMINANT)
                                                                    .build();
    //when
    Set<ConstraintViolation<ExaminantWorkflowCheckModel>> constraintViolations = validator.validate(
        object);

    //then
    assertThat(constraintViolations).hasSize(1);
    List<String> violationMessages = constraintViolations.stream()
                                                         .map(ConstraintViolation::getMessage)
                                                         .collect(Collectors.toList());
    assertThat(violationMessages).contains("Examinant did not accept without comment");
  }

  @Test
  void shouldAcceptBavCheckWithCommentOnRejection() {
    // Given
    ExaminantWorkflowCheckModel object = ExaminantWorkflowCheckModel.builder()
                                                                    .accepted(false)
                                                                    .checkComment("This is bs")
                                                                    .examinant(EXAMINANT)
                                                                    .build();
    //when
    Set<ConstraintViolation<ExaminantWorkflowCheckModel>> constraintViolations = validator.validate(
        object);

    //then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptBavCheckWithEmptyPersonFunction() {
    // Given
    ExaminantWorkflowCheckModel object = ExaminantWorkflowCheckModel.builder()
            .accepted(false)
            .checkComment("This is bs")
            .examinant(PersonModel.builder()
                    .firstName("Marek")
                    .lastName("Hamsik")
                    .personFunction("")
                    .build())
            .build();
    //when
    Set<ConstraintViolation<ExaminantWorkflowCheckModel>> constraintViolations = validator.validate(
            object);

    //then
    assertThat(constraintViolations).hasSize(1);
  }

}