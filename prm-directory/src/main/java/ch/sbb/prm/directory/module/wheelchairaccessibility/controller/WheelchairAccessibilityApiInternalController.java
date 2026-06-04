package ch.sbb.prm.directory.module.wheelchairaccessibility.controller;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadAccessibilityModel;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.prm.directory.module.wheelchairaccessibility.api.WheelchairAccessibilityApiInternal;
import ch.sbb.prm.directory.module.wheelchairaccessibility.mapper.AccessibilityMapper;
import ch.sbb.prm.directory.module.wheelchairaccessibility.service.WheelchairAccessibilityService;
import java.time.LocalDate;
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
  public ReadAccessibilityModel getPlatformAccessibility(String platformSloid, LocalDate startingFrom) {
    Accessibility accessibility = wheelchairAccessibilityService.calculateForPlatform(platformSloid, startingFrom);
    return AccessibilityMapper.toModel(accessibility);
  }

  @Override
  public ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(String stopPointSloid) {
    return ReadWheelchairAccessibilityModel.builder()
        .state(wheelchairAccessibilityService.calculateForStopPointToday(stopPointSloid))
        .build();
  }

  @Override
  public ReadAccessibilityModel getStopPointAccessibility(String stopPointSloid, LocalDate startingFrom) {
    Accessibility accessibility = wheelchairAccessibilityService.calculateForStopPoint(stopPointSloid, startingFrom);
    return AccessibilityMapper.toModel(accessibility);
  }

}
