package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(enumAsRef = true)
@Getter
public enum ContactPointType {
  INFORMATION_DESK,
  TICKET_COUNTER
}
