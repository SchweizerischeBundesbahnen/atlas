package ch.sbb.atlas.imports;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ItemProcessResponseStatus {
  SUCCESS,
  WARNING,
  FAILED
}
