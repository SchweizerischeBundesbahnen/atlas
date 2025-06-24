package ch.sbb.workflow.sepodi.termination.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, example = "STARTED")
public enum TerminationWorkflowStatus {
  STARTED,
  TARIFF_STOP_APPROVED,
  TARIFF_STOP_NOT_APPROVED,
  TERMINATION_APPROVED,
  TERMINATION_NOT_APPROVED,
  TERMINATION_NOT_APPROVED_CLOSED,
  CANCELED
}
