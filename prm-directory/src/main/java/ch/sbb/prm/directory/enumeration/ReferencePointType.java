package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum ReferencePointType {
  MAIN_STATION_ENTRANCE(0),
  ALTERNATIVE_STATION_ENTRANCE(1),
  ASSISTANCE_POINT(2),
  INFORMATION_DESK(3),
  PLATFORM(4),
  NO_REFERENCE_POINT(4);

  private final Integer rank;

}
