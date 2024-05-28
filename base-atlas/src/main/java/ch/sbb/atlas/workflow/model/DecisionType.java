package ch.sbb.atlas.workflow.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum DecisionType {
  VOTED,
  REJECTED,
  RESTATED,
  CANCELED,
  CLOSED
}
