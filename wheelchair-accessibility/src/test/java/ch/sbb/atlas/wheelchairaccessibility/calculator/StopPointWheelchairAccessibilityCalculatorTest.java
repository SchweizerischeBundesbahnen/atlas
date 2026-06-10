package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.calculator.StopPointWheelchairAccessibilityCalculator.WorstAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPointTestData;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StopPointWheelchairAccessibilityCalculatorTest {

  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder().reduced(true).build();

    WheelchairAccessibilityState result = WheelchairAccessibility.calculateStopPointOnDate(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .build());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  @Test
  void shouldReturnWorstCaseAcrossPlatformsForReducedStopPoint() {
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder().reduced(true).build();
    AccessibilityPlatformTestData autonomous = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();
    AccessibilityPlatformTestData shuttle = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();

    WheelchairAccessibilityState result = WheelchairAccessibility.calculateStopPointOnDate(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(autonomous, shuttle))
            .build());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @ParameterizedTest(name = "platform1={0}, platform2={1} -> {2}")
  @CsvSource({
      // Defined by Rank
      "AUTONOMY,            RAMP_USE,          RAMP_USE",
      "RAMP_USE,            PRE_REGISTRATION,  PRE_REGISTRATION",
      "PRE_REGISTRATION,    NO_ACCESS,         NO_ACCESS",
      "NO_INFO,             SHUTTLE,           SHUTTLE",

      // NO_INFO
      "NO_ACCESS,           NO_INFO,           NO_INFO",
      "AUTONOMY,            NO_INFO,           NO_INFO",
  })
  void shouldCalculateWorstAccessibility(WheelchairAccessibilityState platform1, WheelchairAccessibilityState platform2,
      WheelchairAccessibilityState expected) {
    WheelchairAccessibilityState result = new WorstAccessibilityCalculator(List.of(platform1, platform2)).calculate();

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldCalculateWorstAccessibilityWithOnePlatform() {
    WheelchairAccessibilityState result = new WorstAccessibilityCalculator(
        List.of(WheelchairAccessibilityState.AUTONOMY)).calculate();
    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldCalculateWorstAccessibilityWithoutPlatform() {
    WheelchairAccessibilityState result = new WorstAccessibilityCalculator(Collections.emptyList()).calculate();
    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

}
