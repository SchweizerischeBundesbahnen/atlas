package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPointTestData;
import java.util.List;
import org.junit.jupiter.api.Test;

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

}
