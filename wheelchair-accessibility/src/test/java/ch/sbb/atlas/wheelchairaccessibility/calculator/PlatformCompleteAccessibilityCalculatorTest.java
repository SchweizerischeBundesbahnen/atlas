package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelation;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelationTestData;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformCompleteAccessibilityCalculatorTest {

  @Test
  void shouldReturnShuttleWhenShuttleIsYes() {
    AccessibilityPlatform platform = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnNoAccessWhenNoStepFreeAccessRelationExists() {
    AccessibilityPlatform platform = platformBuilder().build();
    List<AccessibilityRelation> relations = List.of(relationWith(StepFreeAccessAttributeType.NO));

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, relations);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoAccessWhenRelationsListIsEmpty() {
    AccessibilityPlatform platform = platformBuilder().build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnRampUseWhenLevelAccessIsYesWithStaffAssistance() {
    AccessibilityPlatform platform = platformBuilder()
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.RAMP_USE);
  }

  @Test
  void shouldReturnAutonomyWhenSuperElevationBelowLimitAndLevelAccessYes() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(39.99D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldReturnPreRegistrationWhenSuperElevationBelowLimitButLevelAccessNotYesAndBoardingDeviceIsLifts() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(39.99D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnPreRegistrationWhenSuperElevationAtOrAboveLimitAndBoardingDeviceIsRamps() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(40.0D)
        .boardingDevice(BoardingDeviceAttributeType.RAMPS)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnNoAccessWhenBoardingDeviceNotLiftOrRampAndLevelAccessIsNo() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
        .boardingDevice(BoardingDeviceAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoAccessWhenBoardingDeviceIsNo() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoInfoWhenNoOtherConditionMatches() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  @Test
  void shouldReturnNoInfoWhenSuperElevationIsNullAndOtherConditionsDoNotMatch() {
    AccessibilityPlatform platform = platformBuilder()
        .superelevation(null)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = PlatformCompleteAccessibilityCalculator.calculate(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  private static AccessibilityPlatformTestData.AccessibilityPlatformTestDataBuilder platformBuilder() {
    return AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.NO);
  }

  private static List<AccessibilityRelation> validRelations() {
    return List.of(relationWith(StepFreeAccessAttributeType.YES));
  }

  private static AccessibilityRelation relationWith(StepFreeAccessAttributeType stepFreeAccess) {
    return AccessibilityRelationTestData.builder().stepFreeAccess(stepFreeAccess).build();
  }
}
