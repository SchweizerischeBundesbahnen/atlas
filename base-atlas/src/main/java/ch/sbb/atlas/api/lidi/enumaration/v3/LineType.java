package ch.sbb.atlas.api.lidi.enumaration.v3;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LineTypeV3", enumAsRef = true)
public enum LineType {
  ORDERLY,
  DISPOSITION,
  TEMPORARY,
  OPERATIONAL,
  TECHNICAL,
  CONCESSION,
  ZONE
}
