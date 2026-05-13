package ch.sbb.prm.directory.module.wheelchairaccessibility.controller;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import ch.sbb.prm.directory.module.wheelchairaccessibility.api.WheelchairAccessibilityApiInternal;
import ch.sbb.prm.directory.module.wheelchairaccessibility.service.WheelchairAccessibilityService;
import java.util.List;
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
    PlatformVersion platform = platformService.findPlatformVersionValidToday(sloid);
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForPlatformToday(platform))
        .build();
  }

  @Override
  public ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(String sloid) {
    StopPointVersion stopPoint = stopPointService.findStopPointVersionValidToday(sloid);
    List<PlatformVersion> platformsToday = platformService.getPlatformsByStopPoint(sloid).stream()
        .filter(platform -> DateRange.fromVersionable(platform).containsToday())
        .toList();
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForStopPointToday(stopPoint, platformsToday))
        .build();
  }

}
