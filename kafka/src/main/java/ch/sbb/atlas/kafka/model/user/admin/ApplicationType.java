package ch.sbb.atlas.kafka.model.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ApplicationType {
  TTFN,
  LIDI,
  BODI,

  ;
}
