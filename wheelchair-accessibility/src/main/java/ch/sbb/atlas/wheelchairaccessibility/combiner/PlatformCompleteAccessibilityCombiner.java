package ch.sbb.atlas.wheelchairaccessibility.combiner;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;

public class PlatformCompleteAccessibilityCombiner {

  public WheelchairAccessibilityState combine(WheelchairAccessibilityState stopPoint, WheelchairAccessibilityState platform) {
    return switch (stopPoint) {
      case AUTONOMY, NO_INFO -> switch (platform) {
        case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
        case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
        case PRE_REGISTRATION -> WheelchairAccessibilityState.PRE_REGISTRATION;
        case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
        case NO_ACCESS -> WheelchairAccessibilityState.NO_ACCESS;
        case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
      };
      case RAMP_USE -> switch (platform) {
        case AUTONOMY, NO_ACCESS, PRE_REGISTRATION, RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
        case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
        case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
      };
      case PRE_REGISTRATION -> switch (platform) {
        case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
        case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
        case PRE_REGISTRATION, NO_ACCESS -> WheelchairAccessibilityState.PRE_REGISTRATION;
        case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
        case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
      };
      case SHUTTLE -> switch (platform) {
        case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
        case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
        case PRE_REGISTRATION, SHUTTLE, NO_ACCESS -> WheelchairAccessibilityState.SHUTTLE;
        case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
      };
      case NO_ACCESS -> switch (platform) {
        case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
        case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
        case PRE_REGISTRATION, NO_ACCESS -> WheelchairAccessibilityState.NO_ACCESS;
        case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
        case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
      };
    };
  }

}
