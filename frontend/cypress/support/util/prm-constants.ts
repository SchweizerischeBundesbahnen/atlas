export default class PrmConstants {
  static basicValues() {
    return ["YES", "TO_BE_COMPLETED", "NO"];
  }

  static basicValuesAndNotApplicable() {
    return this.basicValues().concat("NOT_APPLICABLE");
  }

  static basicValuesAndNotApplicableAndPartially() {
    return this.basicValuesAndNotApplicable().concat("PARTIALLY");
  }

  static basicValuesAndNotApplicableAndWithRemoteControl() {
    return this.basicValuesAndNotApplicable().concat("WITH_REMOTE_CONTROL");
  }

  static infoOpportunitiesValues() {
    return [
      "TO_BE_COMPLETED",
      "STATIC_VISUAL_INFORMATION",
      "ELECTRONIC_VISUAL_INFORMATION_DEPARTURES",
      "ELECTRONIC_VISUAL_INFORMATION_COMPLETE",
      "ACOUSTIC_INFORMATION",
      "TEXT_TO_SPEECH_DEPARTURES",
      "TEXT_TO_SPEECH_COMPLETE"
    ];
  }

  static vehicleAccessValues() {
    return [
      "TO_BE_COMPLETED",
      "PLATFORM_ACCESS_WITHOUT_ASSISTANCE",
      "PLATFORM_ACCESS_WITH_ASSISTANCE",
      "PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED",
      "PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE"
    ];
  }

  static boardingDeviceValues() {
    return ["TO_BE_COMPLETED", "RAMPS", "LIFTS", "NO", "NOT_APPLICABLE"];
  }

  static stepFreeAccessValues() {
    return [
      "TO_BE_COMPLETED",
      "YES",
      "NO",
      "NOT_APPLICABLE",
      "YES_WITH_LIFT",
      "YES_WITH_RAMP"
    ];
  }

  static referencePointTypeValues() {
    return [
      "PLATFORM",
      "MAIN_STATION_ENTRANCE",
      "ASSISTANCE_POINT",
      "INFORMATION_DESK",
      "NO_REFERENCE_POINT",
      "ALTERNATIVE_STATION_ENTRANCE"
    ];
  }

  static meansOfTransport() {
    return [
      "BUS",
      "CABLE_CAR",
      "RACK_RAILWAY",
      "TRAIN",
      "METRO",
      "UNKNOWN",
      "CABLE_RAILWAY",
      "ELEVATOR",
      "CHAIRLIFT",
      "TRAM",
      "BOAT"
    ];
  }

  static booleanValues() {
    return [true, false];
  }
}
