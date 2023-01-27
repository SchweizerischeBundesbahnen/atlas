package ch.sbb.atlas.api.line.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum CoverageType {
  COMPLETE,
  INCOMPLETE
}
