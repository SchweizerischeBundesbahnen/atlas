package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelationTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPointTestData;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformWheelchairAccessibilityCalculatorTest {

  private static final String PLATFORM_SLOID = "ch:1:sloid:7000::1";
  private static final String STOP_POINT_SLOID = "ch:1:sloid:7000";

  @Test
  void shouldUseReducedCalculatorWhenStopPointIsReduced() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .sloid(PLATFORM_SLOID)
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder()
        .sloid(STOP_POINT_SLOID)
        .reduced(true)
        .build();

    WheelchairAccessibilityState result = WheelchairAccessibility.calculatePlatformOnDate(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(platform))
            .build());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldCombinePlatformAndStopPointStatesWhenStopPointIsComplete() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(20.0D)
        .sloid(PLATFORM_SLOID)
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder()
        .sloid(STOP_POINT_SLOID)
        .reduced(false)
        .alternativeTransport(StandardAttributeType.NO)
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();
    List<AccessibilityRelationTestData> relations = List.of(AccessibilityRelationTestData.builder()
        .sloid(PLATFORM_SLOID)
        .stepFreeAccess(StepFreeAccessAttributeType.YES)
        .build());

    WheelchairAccessibilityState result = WheelchairAccessibility.calculatePlatformOnDate(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(platform))
            .relations(relations)
            .build());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldCalculatePlatformAccessibilityWithVersionOnSameRange() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .sloid(PLATFORM_SLOID)
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(20.0D)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder()
        .sloid(STOP_POINT_SLOID)
        .reduced(false)
        .alternativeTransport(StandardAttributeType.NO)
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    List<AccessibilityRelationTestData> relations = List.of(
        AccessibilityRelationTestData.builder()
            .sloid(PLATFORM_SLOID)
            .stepFreeAccess(StepFreeAccessAttributeType.YES)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .build());

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2020, 1, 10));
    Accessibility result = WheelchairAccessibility.calculatePlatform(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(platform))
            .relations(relations)
            .build(), accessibilityFilter);

    Accessibility expectedAccessibility = new Accessibility()
        .with(new DateRange(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 2, 9)), WheelchairAccessibilityState.AUTONOMY);
    assertThat(result).isEqualTo(expectedAccessibility);
  }

  @Test
  void shouldCalculatePlatformAccessibilityWithNoStopPointVersion() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .sloid(PLATFORM_SLOID)
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(20.0D)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2020, 1, 10));
    Accessibility result = WheelchairAccessibility.calculatePlatform(
        AccessibilityRequest.builder()
            .stopPoint(Collections.emptyList())
            .platform(List.of(platform))
            .build(), accessibilityFilter);

    Accessibility expectedAccessibility = new Accessibility()
        .with(new DateRange(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 2, 9)), WheelchairAccessibilityState.NO_INFO);
    assertThat(result).isEqualTo(expectedAccessibility);
  }

  @Test
  void shouldCalculatePlatformAccessibilityWithRelationVersionChanges() {
    AccessibilityPlatformTestData platform = AccessibilityPlatformTestData.builder()
        .sloid(PLATFORM_SLOID)
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .superelevation(20.0D)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.builder()
        .sloid(STOP_POINT_SLOID)
        .reduced(false)
        .alternativeTransport(StandardAttributeType.NO)
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    List<AccessibilityRelationTestData> relations = List.of(
        AccessibilityRelationTestData.builder()
            .sloid(PLATFORM_SLOID)
            .stepFreeAccess(StepFreeAccessAttributeType.YES)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 1, 31))
            .build(),
        AccessibilityRelationTestData.builder()
            .sloid(PLATFORM_SLOID)
            .stepFreeAccess(StepFreeAccessAttributeType.NO)
            .validFrom(LocalDate.of(2020, 2, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .build());

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2020, 1, 10));
    Accessibility result = WheelchairAccessibility.calculatePlatform(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(platform))
            .relations(relations)
            .build(), accessibilityFilter);

    Accessibility expectedAccessibility = new Accessibility()
        .with(new DateRange(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 1, 31)), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 9)), WheelchairAccessibilityState.NO_ACCESS);
    assertThat(result).isEqualTo(expectedAccessibility);
  }

}
