package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WheelchairAccessibilityServiceTest {

  private static final String PLATFORM_SLOID = "plat-1";
  private static final String STOP_POINT_SLOID = "sp-1";

  @Mock
  private StopPointService stopPointService;
  @Mock
  private PlatformService platformService;
  @Mock
  private RelationService relationService;

  private WheelchairAccessibilityService service;

  @BeforeEach
  void setUp() {
    service = new WheelchairAccessibilityService(stopPointService, platformService, relationService);
  }

  @Test
  void shouldDelegateReducedPlatformToCalculator() {
    PlatformVersion platform = PlatformVersion.builder()
        .sloid(PLATFORM_SLOID)
        .parentServicePointSloid(STOP_POINT_SLOID)
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();
    StopPointVersion stopPoint = StopPointVersion.builder()
        .sloid(STOP_POINT_SLOID)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();

    when(stopPointService.findStopPointVersionValidToday(any())).thenReturn(Optional.of(stopPoint));
    when(platformService.findPlatformVersionValidToday(platform.getSloid())).thenReturn(Optional.of(platform));
    when(relationService.findRelationVersionValidTodayByPlatform(any())).thenReturn(List.of());

    assertThat(service.calculateForPlatformToday(platform.getSloid())).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnWorstCaseAcrossPlatforms() {
    StopPointVersion stopPoint = StopPointVersion.builder()
        .sloid(STOP_POINT_SLOID)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();
    PlatformVersion autonomous = PlatformVersion.builder()
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusDays(1))
        .sloid(PLATFORM_SLOID)
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();
    PlatformVersion shuttle = PlatformVersion.builder()
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusDays(1))
        .sloid("plat-2")
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();

    when(stopPointService.findStopPointVersionValidToday(any())).thenReturn(Optional.of(stopPoint));
    when(platformService.getPlatformsByStopPoint(stopPoint.getSloid())).thenReturn(List.of(autonomous, shuttle));
    doReturn(List.of()).when(relationService).getRelationsByParentServicePointSloid(any());

    WheelchairAccessibilityState result = service.calculateForStopPointToday(stopPoint.getSloid());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @Test
  void shouldReturnNoInfoWhenStopPointHasNoPlatforms() {
    StopPointVersion stopPoint = StopPointVersion.builder()
        .sloid(STOP_POINT_SLOID)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();

    WheelchairAccessibilityState result = service.calculateForStopPointToday(stopPoint.getSloid());

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

}
