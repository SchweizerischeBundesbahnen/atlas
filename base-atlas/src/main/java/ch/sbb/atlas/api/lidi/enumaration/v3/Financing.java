package ch.sbb.atlas.api.lidi.enumaration.v3;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Financing {
  RPV,
  WBV,
  SELF_FINANCED,
  LONG_DISTANCE_SERVICES
}
