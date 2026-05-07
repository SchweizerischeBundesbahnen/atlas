package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.StopPointCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.combiner.PlatformCompleteAccessibilityCombiner;
import ch.sbb.prm.directory.module.wheelchairaccessibility.helper.WheelchairAccessibilityDataLoader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WheelchairAccessibilityServiceTest {

  private WheelchairAccessibilityService service;
  private PlatformCompleteAccessibilityCalculator platformCompleteCalculator;
  private StopPointCompleteAccessibilityCalculator stopPointCompleteCalculator;
  private PlatformCompleteAccessibilityCombiner combiner;
  private WheelchairAccessibilityDataLoader dataLoader;

  @BeforeEach
  void setUp() {
    platformCompleteCalculator = mock(PlatformCompleteAccessibilityCalculator.class);
    stopPointCompleteCalculator = mock(StopPointCompleteAccessibilityCalculator.class);
    combiner = mock(PlatformCompleteAccessibilityCombiner.class);
    dataLoader = mock(WheelchairAccessibilityDataLoader.class);

    service = new WheelchairAccessibilityService(
        new PlatformReducedAccessibilityCalculator(),
        platformCompleteCalculator,
        stopPointCompleteCalculator,
        combiner,
        dataLoader
    );
  }

  @Test
  void shouldUseReducedCalculatorWhenStopPointIsReduced() {
    PlatformVersion platform = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = service.calculateForPlatformToday(platform, true);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldDelegateToCompleteCalculatorWhenStopPointIsComplete() {
    PlatformVersion platform = PlatformVersion.builder().sloid("plat-1").parentServicePointSloid("sp-1").build();
    StopPointVersion stopPoint = StopPointVersion.builder().build();
    List<RelationVersion> relations = List.of(RelationVersion.builder().build());

    when(dataLoader.loadStopPointValidToday("sp-1")).thenReturn(stopPoint);
    when(dataLoader.loadRelationsValidToday("plat-1")).thenReturn(relations);
    when(platformCompleteCalculator.calculatePlatform(platform, relations)).thenReturn(WheelchairAccessibilityState.AUTONOMY);
    when(stopPointCompleteCalculator.calculateStopPoint(stopPoint)).thenReturn(WheelchairAccessibilityState.AUTONOMY);
    when(combiner.combine(WheelchairAccessibilityState.AUTONOMY, WheelchairAccessibilityState.AUTONOMY))
        .thenReturn(WheelchairAccessibilityState.AUTONOMY);

    WheelchairAccessibilityState result = service.calculateForPlatformToday(platform, false);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }

  @Test
  void shouldReturnWorstCaseWhenAggregatingMultiplePlatforms() {
    PlatformVersion autonomous = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();
    PlatformVersion noInfo = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = service.calculateForStopPointToday(true, List.of(autonomous, noInfo));

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  @Test
  void shouldReturnShuttleWhenAtLeastOnePlatformHasShuttle() {
    PlatformVersion shuttle = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .build();
    PlatformVersion autonomous = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();

    WheelchairAccessibilityState result = service.calculateForStopPointToday(true, List.of(autonomous, shuttle));

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    WheelchairAccessibilityState result = service.calculateForStopPointToday(true, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

}
