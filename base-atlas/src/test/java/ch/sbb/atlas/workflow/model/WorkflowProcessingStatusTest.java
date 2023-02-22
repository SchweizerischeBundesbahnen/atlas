package ch.sbb.atlas.workflow.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class WorkflowProcessingStatusTest {

  @Test
  public void shouldReturnEvaluatedWhenWorkflowStatusIsApproved() {
    //when
    WorkflowProcessingStatus result = WorkflowProcessingStatus.getProcessingStatus(WorkflowStatus.APPROVED);
    //then
    assertThat(result).isEqualTo(WorkflowProcessingStatus.EVALUATED);
  }

  @Test
  public void shouldReturnEvaluatedWhenWorkflowStatusIsRejected() {
    //when
    WorkflowProcessingStatus result = WorkflowProcessingStatus.getProcessingStatus(WorkflowStatus.REJECTED);
    //then
    assertThat(result).isEqualTo(WorkflowProcessingStatus.EVALUATED);
  }

  @Test
  public void shouldReturnInProgressWhenWorkflowStatusIsRevision() {
    //when
    WorkflowProcessingStatus result = WorkflowProcessingStatus.getProcessingStatus(WorkflowStatus.REVISION);
    //then
    assertThat(result).isEqualTo(WorkflowProcessingStatus.IN_PROGRESS);
  }

  @Test
  public void shouldReturnInProgressWhenWorkflowStatusIsHearing() {
    //when
    WorkflowProcessingStatus result = WorkflowProcessingStatus.getProcessingStatus(WorkflowStatus.HEARING);
    //then
    assertThat(result).isEqualTo(WorkflowProcessingStatus.IN_PROGRESS);
  }

}