package ch.sbb.prm.directory.module.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformCompleteAccessibilityCalculatorTest {

  private final PlatformCompleteAccessibilityCalculator calculator = new PlatformCompleteAccessibilityCalculator();

  @Test
  void shouldReturnShuttleWhenShuttleIsYes() {
    PlatformVersion platform = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnNoAccessWhenNoStepFreeAccessRelationExists() {
    PlatformVersion platform = platformBuilder().build();
    List<RelationVersion> relations = List.of(relationWith(StepFreeAccessAttributeType.NO));

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, relations);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoAccessWhenRelationsListIsEmpty() {
    PlatformVersion platform = platformBuilder().build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnRampUseWhenLevelAccessIsYesWithStaffAssistance() {
    PlatformVersion platform = platformBuilder()
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.RAMP_USE);
  }

  @Test
  void shouldReturnAutonomyWhenSuperElevationBelowLimitAndLevelAccessYes() {
    PlatformVersion platform = platformBuilder()
        .superelevation(39.99D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldReturnPreRegistrationWhenSuperElevationBelowLimitButLevelAccessNotYesAndBoardingDeviceIsLifts() {
    PlatformVersion platform = platformBuilder()
        .superelevation(39.99D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnPreRegistrationWhenSuperElevationAtOrAboveLimitAndBoardingDeviceIsRamps() {
    PlatformVersion platform = platformBuilder()
        .superelevation(40.0D)
        .boardingDevice(BoardingDeviceAttributeType.RAMPS)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnNoAccessWhenBoardingDeviceNotLiftOrRampAndLevelAccessIsNo() {
    PlatformVersion platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
        .boardingDevice(BoardingDeviceAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoAccessWhenBoardingDeviceIsNo() {
    PlatformVersion platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoInfoWhenNoOtherConditionMatches() {
    PlatformVersion platform = platformBuilder()
        .superelevation(50.0D)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  @Test
  void shouldReturnNoInfoWhenSuperElevationIsNullAndOtherConditionsDoNotMatch() {
    PlatformVersion platform = platformBuilder()
        .superelevation(null)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = calculator.calculatePlatform(platform, validRelations());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  private static PlatformVersion.PlatformVersionBuilder<?, ?> platformBuilder() {
    return PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.NO);
  }

  private static List<RelationVersion> validRelations() {
    return List.of(relationWith(StepFreeAccessAttributeType.YES));
  }

  private static RelationVersion relationWith(StepFreeAccessAttributeType stepFreeAccess) {
    return RelationVersion.builder().stepFreeAccess(stepFreeAccess).build();
  }
}
