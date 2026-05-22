package ch.sbb.prm.directory.module.wheelchairaccessibility.controller;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import ch.sbb.prm.directory.module.wheelchairaccessibility.api.WheelchairAccessibilityApiInternal;
import ch.sbb.prm.directory.module.wheelchairaccessibility.service.WheelchairAccessibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WheelchairAccessibilityApiInternalController implements WheelchairAccessibilityApiInternal {

  private final WheelchairAccessibilityService wheelchairAccessibilityService;

  @Override
  public ReadWheelchairAccessibilityModel getPlatformAccessibilityToday(String platformSloid) {
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForPlatformToday(platformSloid))
        .build();
  }

  @Override
  public ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(String stopPointSloid) {
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForStopPointToday(stopPointSloid))
        .build();
  }

}
