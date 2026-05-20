package ch.sbb.prm.directory.module.wheelchairaccessibility.controller;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import ch.sbb.prm.directory.module.wheelchairaccessibility.api.WheelchairAccessibilityApiInternal;
import ch.sbb.prm.directory.module.wheelchairaccessibility.service.WheelchairAccessibilityService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WheelchairAccessibilityApiInternalController implements WheelchairAccessibilityApiInternal {

  private final WheelchairAccessibilityService wheelchairAccessibilityService;
  private final PlatformService platformService;
  private final StopPointService stopPointService;

  @Override
  public ReadWheelchairAccessibilityModel getPlatformAccessibilityToday(String sloid) {
    Optional<PlatformVersion> platform = platformService.findPlatformVersionValidToday(sloid);
    if (platform.isEmpty()) {
      return buildNoInfoWheelchairAccessibilityModel();
    }
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForPlatformToday(platform.get()))
        .build();
  }

  @Override
  public ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(String sloid) {
    Optional<StopPointVersion> stopPoint = stopPointService.findStopPointVersionValidToday(sloid);
    if (stopPoint.isEmpty()) {
      return buildNoInfoWheelchairAccessibilityModel();
    }
    List<PlatformVersion> platformsToday = platformService.getPlatformsByStopPoint(sloid).stream()
        .filter(platform -> DateRange.fromVersionable(platform).containsToday())
        .toList();
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForStopPointToday(stopPoint.get(), platformsToday))
        .build();
  }

  private ReadWheelchairAccessibilityModel buildNoInfoWheelchairAccessibilityModel() {
    return ReadWheelchairAccessibilityModel.builder()
        .state(WheelchairAccessibilityState.NO_INFO)
        .build();
  }

}
