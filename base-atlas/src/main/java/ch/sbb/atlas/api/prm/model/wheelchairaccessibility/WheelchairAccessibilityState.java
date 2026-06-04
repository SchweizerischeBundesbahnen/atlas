package ch.sbb.atlas.api.prm.model.wheelchairaccessibility;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "AUTONOMY")
@Getter
@RequiredArgsConstructor
public enum WheelchairAccessibilityState {
  AUTONOMY(11),
  RAMP_USE(12),
  PRE_REGISTRATION(13),
  NO_ACCESS(14),
  NO_INFO(15),
  SHUTTLE(1000);

  private final Integer rank;

  public static WheelchairAccessibilityState of(Integer value) {
    if (value == null) {
      return null;
    }
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }
}
