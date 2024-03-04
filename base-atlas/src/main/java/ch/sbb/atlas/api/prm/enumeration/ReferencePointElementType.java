package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "PLATFORM")
@Getter
@RequiredArgsConstructor
public enum ReferencePointElementType {

  PLATFORM,
  CONTACT_POINT,
  TOILET,
  PARKING_LOT,

  @Deprecated(since = "01.01.2024")
  TICKET_COUNTER,

  @Deprecated(since = "01.01.2024")
  INFO_DESK,
}
