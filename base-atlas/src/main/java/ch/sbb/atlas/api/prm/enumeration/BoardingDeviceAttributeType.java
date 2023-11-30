package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "LIFTS")
@Getter
@RequiredArgsConstructor
// Ranks from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts
public enum BoardingDeviceAttributeType {
  TO_BE_COMPLETED(0),
  NO(2),
  NOT_APPLICABLE(3),
  RAMPS(9),
  LIFTS(10);

  private final Integer rank;

  public static BoardingDeviceAttributeType of(Integer value) {
    if (value == null) {
      return null;
    }
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }

}
