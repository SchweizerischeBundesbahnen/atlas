package ch.sbb.atlas.workflow.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
/**
 * <a href="https://confluence.sbb.ch/display/ATLAS/Status+auf+Businessobjekten">Status Documentation</a>
 */
public enum WorkflowStatus {
  ADDED,
  STARTED,
  REVISION,
  HEARING,
  APPROVED,
  REJECTED,
  REVOKED
}
