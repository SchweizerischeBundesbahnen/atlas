package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "MAIN_STATION_ENTRANCE")
@Getter
@RequiredArgsConstructor
public enum ReferencePointAttributeType {
  MAIN_STATION_ENTRANCE(0),
  ALTERNATIVE_STATION_ENTRANCE(1),
  ASSISTANCE_POINT(2),
  INFORMATION_DESK(3),
  PLATFORM(4),
  NO_REFERENCE_POINT(5);

  private final Integer rank;

}
