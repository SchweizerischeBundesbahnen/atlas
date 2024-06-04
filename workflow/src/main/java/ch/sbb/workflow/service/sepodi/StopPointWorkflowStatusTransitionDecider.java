package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.exception.StopPointPointStatusChangeNotAllowedException;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointWorkflowStatusTransitionDecider {

  private final Set<WorkflowStatus> FROM_STATUS_ADDED_TO_STATUS_ALLOWED = Set.of(WorkflowStatus.REJECTED, WorkflowStatus.HEARING);
  private final Set<WorkflowStatus> FROM_STATUS_HEARING_TO_STATUS_ALLOWED = Set.of(WorkflowStatus.CANCELED,
      WorkflowStatus.REJECTED, WorkflowStatus.APPROVED);

  public void validateWorkflowStatusTransition(WorkflowStatus fromStatus, WorkflowStatus toStatus) {
    switch (fromStatus) {
      case ADDED -> fromStatusAddedTo(toStatus);
      case HEARING -> fromStatusHaringTo(toStatus);
      default -> throw new StopPointPointStatusChangeNotAllowedException(fromStatus, toStatus);
    }

  }

  private void fromStatusAddedTo(WorkflowStatus toStatus) {
    boolean statusTransitionAllowed = FROM_STATUS_ADDED_TO_STATUS_ALLOWED.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new StopPointPointStatusChangeNotAllowedException(WorkflowStatus.ADDED, toStatus);
    }
  }

  private void fromStatusHaringTo(WorkflowStatus toStatus) {
    boolean statusTransitionAllowed = FROM_STATUS_HEARING_TO_STATUS_ALLOWED.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new StopPointPointStatusChangeNotAllowedException(WorkflowStatus.HEARING, toStatus);
    }
  }

}
