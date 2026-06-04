package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WheelchairAccessibilityCombinerTest {

  @ParameterizedTest(name = "stopPoint={0}, platform={1} -> {2}")
  @CsvSource({
      // StopPoint = AUTONOMY
      "AUTONOMY,         AUTONOMY,         AUTONOMY",
      "AUTONOMY,         RAMP_USE,         RAMP_USE",
      "AUTONOMY,         PRE_REGISTRATION, PRE_REGISTRATION",
      "AUTONOMY,         SHUTTLE,          SHUTTLE",
      "AUTONOMY,         NO_ACCESS,        NO_ACCESS",
      "AUTONOMY,         NO_INFO,          NO_INFO",
      // StopPoint = RAMP_USE
      "RAMP_USE,         AUTONOMY,         RAMP_USE",
      "RAMP_USE,         RAMP_USE,         RAMP_USE",
      "RAMP_USE,         PRE_REGISTRATION, RAMP_USE",
      "RAMP_USE,         SHUTTLE,          SHUTTLE",
      "RAMP_USE,         NO_ACCESS,        RAMP_USE",
      "RAMP_USE,         NO_INFO,          NO_INFO",
      // StopPoint = PRE_REGISTRATION
      "PRE_REGISTRATION, AUTONOMY,         AUTONOMY",
      "PRE_REGISTRATION, RAMP_USE,         RAMP_USE",
      "PRE_REGISTRATION, PRE_REGISTRATION, PRE_REGISTRATION",
      "PRE_REGISTRATION, SHUTTLE,          SHUTTLE",
      "PRE_REGISTRATION, NO_ACCESS,        PRE_REGISTRATION",
      "PRE_REGISTRATION, NO_INFO,          NO_INFO",
      // StopPoint = SHUTTLE
      "SHUTTLE,          AUTONOMY,         AUTONOMY",
      "SHUTTLE,          RAMP_USE,         RAMP_USE",
      "SHUTTLE,          PRE_REGISTRATION, SHUTTLE",
      "SHUTTLE,          SHUTTLE,          SHUTTLE",
      "SHUTTLE,          NO_ACCESS,        SHUTTLE",
      "SHUTTLE,          NO_INFO,          NO_INFO",
      // StopPoint = NO_ACCESS
      "NO_ACCESS,        AUTONOMY,         AUTONOMY",
      "NO_ACCESS,        RAMP_USE,         RAMP_USE",
      "NO_ACCESS,        PRE_REGISTRATION, NO_ACCESS",
      "NO_ACCESS,        SHUTTLE,          SHUTTLE",
      "NO_ACCESS,        NO_ACCESS,        NO_ACCESS",
      "NO_ACCESS,        NO_INFO,          NO_INFO",
      // StopPoint = NO_INFO
      "NO_INFO,          AUTONOMY,         AUTONOMY",
      "NO_INFO,          RAMP_USE,         RAMP_USE",
      "NO_INFO,          PRE_REGISTRATION, PRE_REGISTRATION",
      "NO_INFO,          SHUTTLE,          SHUTTLE",
      "NO_INFO,          NO_ACCESS,        NO_ACCESS",
      "NO_INFO,          NO_INFO,          NO_INFO"
  })
  void shouldCombineCorrectly(WheelchairAccessibilityState stopPoint,
      WheelchairAccessibilityState platform,
      WheelchairAccessibilityState expected) {
    WheelchairAccessibilityState result = WheelchairAccessibilityCombiner.combine(stopPoint, platform);

    assertThat(result).isEqualTo(expected);
  }

}
