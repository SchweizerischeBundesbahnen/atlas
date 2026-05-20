package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WheelchairAccessibilityServiceTest {

  private WheelchairAccessibilityService service;
  private StopPointService stopPointService;
  private RelationService relationService;

  @BeforeEach
  void setUp() {
    stopPointService = mock(StopPointService.class);
    relationService = mock(RelationService.class);
    service = new WheelchairAccessibilityService(stopPointService, relationService);
  }

  @Test
  void shouldDelegateReducedPlatformToCalculator() {
    PlatformVersion platform = PlatformVersion.builder()
        .sloid("plat-1")
        .parentServicePointSloid("sp-1")
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();
    StopPointVersion stopPoint = StopPointVersion.builder().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();

    when(stopPointService.findStopPointVersionValidToday("sp-1")).thenReturn(Optional.of(stopPoint));
    when(relationService.findRelationVersionValidTodayByPlatform("plat-1")).thenReturn(List.of());

    assertThat(service.calculateForPlatformToday(platform)).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnWorstCaseAcrossPlatforms() {
    StopPointVersion stopPoint = StopPointVersion.builder().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    PlatformVersion autonomous = PlatformVersion.builder()
        .sloid("plat-1")
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();
    PlatformVersion shuttle = PlatformVersion.builder()
        .sloid("plat-2")
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();
    when(relationService.findRelationVersionValidTodayByPlatform("plat-1")).thenReturn(List.of());
    when(relationService.findRelationVersionValidTodayByPlatform("plat-2")).thenReturn(List.of());

    WheelchairAccessibilityState result = service.calculateForStopPointToday(stopPoint, List.of(autonomous, shuttle));

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    StopPointVersion stopPoint = StopPointVersion.builder().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();

    WheelchairAccessibilityState result = service.calculateForStopPointToday(stopPoint, List.of());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

}
