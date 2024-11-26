package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum LineType {
  ORDERLY, DISPOSITION, TEMPORARY, OPERATIONAL
}
