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
      case ADDED -> fromStatusTo(WorkflowStatus.ADDED, toStatus, FROM_STATUS_ADDED_TO_STATUS_ALLOWED);
      case HEARING -> fromStatusTo(WorkflowStatus.HEARING, toStatus, FROM_STATUS_HEARING_TO_STATUS_ALLOWED);
      default -> throw new StopPointPointStatusChangeNotAllowedException(fromStatus, toStatus);
    }

  }

  private void fromStatusTo(WorkflowStatus fromStatus, WorkflowStatus toStatus, Set<WorkflowStatus> allowedStatusList) {
    boolean statusTransitionAllowed = allowedStatusList.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new StopPointPointStatusChangeNotAllowedException(fromStatus, toStatus);
    }
  }
  
}
