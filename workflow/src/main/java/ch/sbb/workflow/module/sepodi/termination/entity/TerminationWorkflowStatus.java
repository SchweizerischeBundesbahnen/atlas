package ch.sbb.workflow.module.sepodi.termination.entity;

import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowStatusChangeNotAllowedException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(enumAsRef = true, example = "STARTED")
public enum TerminationWorkflowStatus {
  STARTED,
  TARIFF_STOP_APPROVED,
  TARIFF_STOP_NOT_APPROVED,
  TERMINATION_APPROVED,
  TERMINATION_NOT_APPROVED,
  TERMINATION_NOT_APPROVED_CLOSED,
  CANCELED;

  private static final Set<TerminationWorkflowStatus> ABORTIBLE_WORKFLOW = Set.of(STARTED, TARIFF_STOP_APPROVED,
      TERMINATION_NOT_APPROVED);
  private static final Set<TerminationWorkflowStatus> FROM_STATUS_STARTED_TO_STATUS_ALLOWED =
      Set.of(TARIFF_STOP_NOT_APPROVED, TARIFF_STOP_APPROVED, CANCELED);
  private static final Set<TerminationWorkflowStatus> FROM_STATUS_TARIFF_STOP_APPROVED_TO_STATUS_ALLOWED =
      Set.of(TERMINATION_APPROVED, TERMINATION_NOT_APPROVED, CANCELED);
  private static final Set<TerminationWorkflowStatus> FROM_STATUS_TERMINATION_NOT_APPROVED_TO_STATUS_ALLOWED =
      Set.of(TERMINATION_APPROVED, TERMINATION_NOT_APPROVED_CLOSED);

  public static final Set<TerminationWorkflowStatus> WORKFLOW_IN_PROGRESS = Set.of(STARTED, TARIFF_STOP_APPROVED);

  public static void validateTerminationIsAbortible(TerminationWorkflowStatus status) {
    if (!ABORTIBLE_WORKFLOW.contains(status)) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(status);
    }
  }

  /**
   * @see <a
   *     href="https://confluence.sbb.ch/pages/viewpage
   *     .action?pageId=3057021043&spaceKey=ATLAS&title=ADR-0031%2BServicePoint%2BWorkflow%2BHaltestellen
   *     %2BTerminierungsworkflow#ADR0031:ServicePointWorkflow(HaltestellenTerminierungsworkflow)-Status">
   *     Termination Status Transition Flow</a>
   */
  public static void validateWorkflowStatusTransition(TerminationWorkflowStatus fromStatus, TerminationWorkflowStatus toStatus) {
    switch (fromStatus) {
      case STARTED -> fromStatusTo(STARTED, toStatus, FROM_STATUS_STARTED_TO_STATUS_ALLOWED);
      case TARIFF_STOP_APPROVED ->
          fromStatusTo(TARIFF_STOP_APPROVED, toStatus, FROM_STATUS_TARIFF_STOP_APPROVED_TO_STATUS_ALLOWED);
      case TERMINATION_NOT_APPROVED ->
          fromStatusTo(TERMINATION_NOT_APPROVED, toStatus, FROM_STATUS_TERMINATION_NOT_APPROVED_TO_STATUS_ALLOWED);
      default -> throw new TerminationStopPointWorkflowStatusChangeNotAllowedException(fromStatus, toStatus);
    }
  }

  private static void fromStatusTo(TerminationWorkflowStatus fromStatus, TerminationWorkflowStatus toStatus,
      Set<TerminationWorkflowStatus> allowedStatusList) {
    boolean statusTransitionAllowed = allowedStatusList.contains(toStatus);
    if (!statusTransitionAllowed) {
      throw new TerminationStopPointWorkflowStatusChangeNotAllowedException(fromStatus, toStatus);
    }
  }

}
