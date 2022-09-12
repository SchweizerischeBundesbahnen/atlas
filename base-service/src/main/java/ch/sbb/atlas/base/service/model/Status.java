package ch.sbb.atlas.base.service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Status {
  ACTIVE,
  INACTIVE,
  NEEDS_REVIEW,
  IN_REVIEW,
  REVIEWED
}
