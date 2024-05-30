package ch.sbb.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum JudgementType {
  YES,
  NO
}
