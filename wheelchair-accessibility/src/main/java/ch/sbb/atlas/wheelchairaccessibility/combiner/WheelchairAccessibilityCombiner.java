package ch.sbb.atlas.wheelchairaccessibility.combiner;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;

public final class WheelchairAccessibilityCombiner {

  private WheelchairAccessibilityCombiner() {
  }

  public static WheelchairAccessibilityState combine(WheelchairAccessibilityState stopPointState,
      WheelchairAccessibilityState platformState) {
    return switch (stopPointState) {
      case AUTONOMY, NO_INFO -> platformState;
      case RAMP_USE -> resolveForRampUseStopPoint(platformState);
      case PRE_REGISTRATION -> resolveForPreRegistrationStopPoint(platformState);
      case SHUTTLE -> resolveForShuttleStopPoint(platformState);
      case NO_ACCESS -> resolveForNoAccessStopPoint(platformState);
    };
  }

  private static WheelchairAccessibilityState resolveForRampUseStopPoint(WheelchairAccessibilityState platformState) {
    return switch (platformState) {
      case AUTONOMY, NO_ACCESS, PRE_REGISTRATION, RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
      case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
      case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
    };
  }

  private static WheelchairAccessibilityState resolveForPreRegistrationStopPoint(WheelchairAccessibilityState platformState) {
    return switch (platformState) {
      case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
      case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
      case PRE_REGISTRATION, NO_ACCESS -> WheelchairAccessibilityState.PRE_REGISTRATION;
      case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
      case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
    };
  }

  private static WheelchairAccessibilityState resolveForShuttleStopPoint(WheelchairAccessibilityState platformState) {
    return switch (platformState) {
      case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
      case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
      case PRE_REGISTRATION, SHUTTLE, NO_ACCESS -> WheelchairAccessibilityState.SHUTTLE;
      case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
    };
  }

  private static WheelchairAccessibilityState resolveForNoAccessStopPoint(WheelchairAccessibilityState platformState) {
    return switch (platformState) {
      case AUTONOMY -> WheelchairAccessibilityState.AUTONOMY;
      case RAMP_USE -> WheelchairAccessibilityState.RAMP_USE;
      case PRE_REGISTRATION, NO_ACCESS -> WheelchairAccessibilityState.NO_ACCESS;
      case SHUTTLE -> WheelchairAccessibilityState.SHUTTLE;
      case NO_INFO -> WheelchairAccessibilityState.NO_INFO;
    };
  }

}
