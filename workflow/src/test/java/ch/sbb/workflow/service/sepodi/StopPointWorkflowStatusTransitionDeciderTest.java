package ch.sbb.workflow.service.sepodi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.exception.StopPointPointStatusChangeNotAllowedException;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowStatusTransitionDecider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StopPointWorkflowStatusTransitionDeciderTest {


  @ParameterizedTest
  @EnumSource(value = WorkflowStatus.class, names = {"REJECTED","HEARING"})
  void shouldValidateWorkflowStatusTransitionFromAddedToAllowedStatus(WorkflowStatus status) {
    //when && then
    assertDoesNotThrow(() -> StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(WorkflowStatus.ADDED, status));
  }


  @ParameterizedTest
  @EnumSource(value = WorkflowStatus.class, names = {"ADDED","CANCELED","APPROVED"})
  void shouldNotValidateWorkflowStatusTransitionFromAddedToNotAllowedStatus(WorkflowStatus status){
    //when && then
    assertThrows(StopPointPointStatusChangeNotAllowedException.class,
        () -> StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(WorkflowStatus.ADDED, status));
  }

  @ParameterizedTest
  @EnumSource(value = WorkflowStatus.class, names = {"REJECTED","CANCELED","APPROVED"})
  void shouldValidateWorkflowStatusTransitionFromHearingToAllowedStatus(WorkflowStatus status) {
    //when && then
    assertDoesNotThrow(() -> StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(WorkflowStatus.HEARING, status));
  }

  @ParameterizedTest
  @EnumSource(value = WorkflowStatus.class, names = {"ADDED","HEARING"})
  void shouldNotValidateWorkflowStatusTransitionFromHearingToNotAllowedStatus(WorkflowStatus status){
    //when && then
    assertThrows(StopPointPointStatusChangeNotAllowedException.class,
        () -> StopPointWorkflowStatusTransitionDecider.validateWorkflowStatusTransition(WorkflowStatus.HEARING, status));
  }

}