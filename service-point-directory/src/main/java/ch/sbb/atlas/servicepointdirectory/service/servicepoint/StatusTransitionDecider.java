package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusChangeNotAllowedException;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatusTransitionDecider {

  private final Set<Status> FROM_STATUS_DRAFT_TO_STATUS_ALLOWED = Set.of(Status.IN_REVIEW, Status.VALIDATED);

  public void validateStatusTransition(Status fromStatus, Status toStatus) {
    switch (fromStatus) {
      case DRAFT -> fromStatusDraftTo(toStatus);
      default -> throw new ServicePointStatusChangeNotAllowedException(fromStatus, toStatus);
    }

  }

  private void fromStatusDraftTo(Status toStatus) {
    boolean statusTransitionAllowed = FROM_STATUS_DRAFT_TO_STATUS_ALLOWED.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new ServicePointStatusChangeNotAllowedException(Status.DRAFT, toStatus);
    }
  }

}
