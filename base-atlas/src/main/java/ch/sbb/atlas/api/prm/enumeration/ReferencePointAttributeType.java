package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Schema(enumAsRef = true, example = "MAIN_STATION_ENTRANCE")
@Getter
@RequiredArgsConstructor
// Ranks from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts#34
public enum ReferencePointAttributeType {
  MAIN_STATION_ENTRANCE(0),
  ALTERNATIVE_STATION_ENTRANCE(1),
  ASSISTANCE_POINT(2),

  //Unklar ob hier Information_desk ersetzt werden muss durch contact_point
  INFORMATION_DESK(3),
  PLATFORM(4),
  NO_REFERENCE_POINT(5);

  private final Integer rank;

  public static ReferencePointAttributeType of(Integer value) {
    if (value == null) {
      return null;
    }
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }

}
