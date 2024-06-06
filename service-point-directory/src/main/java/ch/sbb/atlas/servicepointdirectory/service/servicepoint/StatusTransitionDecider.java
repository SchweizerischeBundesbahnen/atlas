package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusChangeNotAllowedException;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatusTransitionDecider {

  private final Set<Status> FROM_STATUS_DRAFT_TO_STATUS_ALLOWED = Set.of(Status.IN_REVIEW, Status.VALIDATED);
  private final Set<Status> FROM_STATUS_IN_REVIEW_TO_STATUS_ALLOWED = Set.of(Status.DRAFT, Status.VALIDATED);

  public void validateWorkflowStatusTransition(Status fromStatus, Status toStatus) {
    switch (fromStatus) {
      case DRAFT -> fromStatusTo(Status.DRAFT, toStatus, FROM_STATUS_DRAFT_TO_STATUS_ALLOWED);
      case IN_REVIEW -> fromStatusTo(Status.IN_REVIEW, toStatus, FROM_STATUS_IN_REVIEW_TO_STATUS_ALLOWED);
      default -> throw new ServicePointStatusChangeNotAllowedException(fromStatus, toStatus);
    }

  }

  private void fromStatusTo(Status fromStatus, Status toStatus, Set<Status> allowedStatusList) {
    boolean statusTransitionAllowed = allowedStatusList.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new ServicePointStatusChangeNotAllowedException(fromStatus, toStatus);
    }
  }

}
