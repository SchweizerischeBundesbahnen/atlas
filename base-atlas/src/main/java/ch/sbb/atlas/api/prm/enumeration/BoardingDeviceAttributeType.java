package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "LIFTS")
@Getter
@RequiredArgsConstructor
public enum BoardingDeviceAttributeType {
  TO_BE_COMPLETED(0),
  NO(1),
  NOT_APPLICABLE(2),
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
