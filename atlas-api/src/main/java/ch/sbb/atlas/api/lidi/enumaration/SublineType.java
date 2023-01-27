package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SublineType {
  TECHNICAL, COMPENSATION, CONCESSION
}
