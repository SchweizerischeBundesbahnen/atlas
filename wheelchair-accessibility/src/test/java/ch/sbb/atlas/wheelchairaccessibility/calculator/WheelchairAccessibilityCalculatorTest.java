package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelationTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPointTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformWithRelations;
import java.util.List;
import org.junit.jupiter.api.Test;

class WheelchairAccessibilityCalculatorTest {

  @Test
  void shouldUseReducedCalculatorWhenStopPointIsReduced() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder().reduced(true).build();

    WheelchairAccessibilityState result = WheelchairAccessibilityCalculator.calculateForPlatform(platform, stopPoint, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldCombinePlatformAndStopPointStatesWhenStopPointIsComplete() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(20.0D)
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder()
        .reduced(false)
        .alternativeTransport(StandardAttributeType.NO)
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();
    List<AccessibilityRelationTestData> relations = List.of(
        AccessibilityRelationTestData.builder().stepFreeAccess(StepFreeAccessAttributeType.YES).build());

    WheelchairAccessibilityState result = WheelchairAccessibilityCalculator.calculateForPlatform(platform, stopPoint, relations);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder().reduced(true).build();

    WheelchairAccessibilityState result = WheelchairAccessibilityCalculator.calculateForStopPoint(stopPoint, List.of());

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

    WheelchairAccessibilityState result = WheelchairAccessibilityCalculator.calculateForStopPoint(stopPoint, List.of(
        new PlatformWithRelations(autonomous, List.of()),
        new PlatformWithRelations(shuttle, List.of())));

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

}
