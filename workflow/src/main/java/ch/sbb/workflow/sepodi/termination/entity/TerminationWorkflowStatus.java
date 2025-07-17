package ch.sbb.workflow.sepodi.termination.entity;

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
  CANCELED,

  ;

  public static final Set<TerminationWorkflowStatus> WORKFLOW_IN_PROGRESS = Set.of(STARTED, TARIFF_STOP_APPROVED);
}
