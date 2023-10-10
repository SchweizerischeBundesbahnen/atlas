package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "PLATFORM")
@Getter
@RequiredArgsConstructor
public enum ReferencePointElementType {

  PLATFORM,
  TICKET_COUNTER,
  INFORMATION_DESK,
  TOILET,
  PARKING_LOT;

}
