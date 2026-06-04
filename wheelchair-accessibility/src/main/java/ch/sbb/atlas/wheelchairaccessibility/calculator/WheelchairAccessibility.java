package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WheelchairAccessibility {

  private static final PlatformWheelchairAccessibilityCalculator PLATFORM = new PlatformWheelchairAccessibilityCalculator();
  private static final StopPointWheelchairAccessibilityCalculator STOP_POINT = new StopPointWheelchairAccessibilityCalculator();

  public static Accessibility calculatePlatform(AccessibilityRequest request, AccessibilityFilter filter) {
    return PLATFORM.calculate(request, filter);
  }

  public static WheelchairAccessibilityState calculatePlatformOnDate(AccessibilityRequest request) {
    return PLATFORM.calculateOnDate(request);
  }

  public static Accessibility calculateStopPoint(AccessibilityRequest request, AccessibilityFilter filter) {
    return STOP_POINT.calculate(request, filter);
  }

  public static WheelchairAccessibilityState calculateStopPointOnDate(AccessibilityRequest request) {
    return STOP_POINT.calculateOnDate(request);
  }

}
