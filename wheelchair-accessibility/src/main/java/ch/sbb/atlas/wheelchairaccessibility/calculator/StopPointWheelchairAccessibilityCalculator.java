package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import java.util.Comparator;
import java.util.List;

class StopPointWheelchairAccessibilityCalculator extends WheelchairAccessibilityCalculator {

  @Override
  WheelchairAccessibilityState calculateOnDate(AccessibilityRequest accessibilityRequest) {
    if (accessibilityRequest.getStopPoint().size() != 1 || accessibilityRequest.getPlatform().isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }

    AccessibilityStopPoint accessibilityStopPoint = accessibilityRequest.getStopPoint().getFirst();

    return accessibilityRequest.getPlatform().stream()
        .map(platform -> calculatePlatformAccessibility(accessibilityRequest, platform, accessibilityStopPoint))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }

  private WheelchairAccessibilityState calculatePlatformAccessibility(AccessibilityRequest accessibilityRequest,
      AccessibilityPlatform platform, AccessibilityStopPoint accessibilityStopPoint) {

    AccessibilityRequest plattformAccessibilityRequest = AccessibilityRequest.builder()
        .stopPoint(List.of(accessibilityStopPoint))
        .platform(List.of(platform))
        .relations(accessibilityRequest.getRelationsOfPlatform(platform.getSloid()))
        .build();

    return WheelchairAccessibility.calculatePlatformOnDate(plattformAccessibilityRequest);
  }

}
