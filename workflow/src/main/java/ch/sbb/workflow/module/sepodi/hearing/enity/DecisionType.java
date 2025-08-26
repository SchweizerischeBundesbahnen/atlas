package ch.sbb.workflow.module.sepodi.hearing.enity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum DecisionType {
  VOTED,
  REJECTED,
  RESTARTED,
  CANCELED,
  VOTED_EXPIRATION
}
