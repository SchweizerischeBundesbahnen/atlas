package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WheelchairAccessibilityServiceTest {

  private WheelchairAccessibilityService service;
  private PlatformCompleteAccessibilityCalculator completeCalculator;

  @BeforeEach
  void setUp() {
    completeCalculator = mock(PlatformCompleteAccessibilityCalculator.class);
    service = new WheelchairAccessibilityService(new PlatformReducedAccessibilityCalculator(), completeCalculator);
  }

  @Test
  void shouldUseReducedCalculatorWhenStopPointIsReduced() {
    PlatformVersion platform = PlatformVersion.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = service.calculateForPlatform(platform, true);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldDelegateToCompleteCalculatorWhenStopPointIsComplete() {
    PlatformVersion platform = PlatformVersion.builder().build();
    when(completeCalculator.calculate(platform)).thenReturn(WheelchairAccessibilityState.AUTONOMY);

    WheelchairAccessibilityState result = service.calculateForPlatform(platform, false);

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

    WheelchairAccessibilityState result = service.calculateForStopPoint(true, List.of(autonomous, noInfo));

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

    WheelchairAccessibilityState result = service.calculateForStopPoint(true, List.of(autonomous, shuttle));

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  //TODO prüfen ob "NO_INFO" fachlich korrekt ist.
  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    WheelchairAccessibilityState result = service.calculateForStopPoint(true, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  private static StopPointVersion stopPointWithMeans(MeanOfTransport mean) {
    return StopPointVersion.builder().meansOfTransport(Set.of(mean)).build();
  }

}
