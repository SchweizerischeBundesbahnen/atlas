package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType.YES;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAddress;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAssistance;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadContactPoint;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadParking;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadQuay;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStationEquipment;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadToilet;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
class ErsadGoldenFixtures {

  static final LocalDateTime PUBLICATION_TIMESTAMP = LocalDateTime.of(2026, Month.SEPTEMBER, 16, 3, 0);

  private static final String YVERDON_ADVICE = "Der Ein- / Ausstieg ist bei entsprechenden Fahrzeugen barrierefrei "
      + "moeglich. Bitte beachten Sie die Information im Fahrplan und melden Sie sich noetigenfalls bis spaetestens "
      + "1 Stunde vor Abfahrt unter swisspass.ch/handicap oder beim Contact Center Handicap taeglich 5.00-24.00 Uhr, "
      + "Telefon 0800 007 102.";

  static ErsadStation tavannes() {
    String sloid = "ch:1:sloid:100";

    return new ErsadStation(
        sloid, 8500100, "Tavannes", 7.20164539670, 47.21984467076,
        new ErsadAddress("Rue de la Gare", "2710", "Tavannes"),
        "https://www.sbb.ch/bahnhof/tavannes",
        List.of("TRAIN"),
        NetexTriState.TRUE,
        new ErsadAssistance(StandardAttributeType.YES, "Bedingungen Hilfestellung (assistanceCondition)", null,
            BooleanOptionalAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED, null),
        new ErsadStationEquipment(StandardAttributeType.YES, StandardAttributeType.TO_BE_COMPLETED,
            StandardAttributeType.YES, YES, StandardAttributeType.TO_BE_COMPLETED, StandardAttributeType.YES),
        List.of(
            new ErsadQuay(sloid + ":0:3", "3", BoardingDeviceAttributeType.NO,
                "Hinweise zum Zugang (adviceAccessInfo)", YES, LevelAccessWheelchairAttributeType.NO, null,
                BooleanOptionalAttributeType.TO_BE_COMPLETED, 1),
            new ErsadQuay(sloid + ":0:2", "2", BoardingDeviceAttributeType.TO_BE_COMPLETED, null,
                BooleanOptionalAttributeType.NO, LevelAccessWheelchairAttributeType.TO_BE_COMPLETED, null, null, 1),
            new ErsadQuay(sloid + ":0:782050", null, BoardingDeviceAttributeType.NOT_APPLICABLE, null,
                BooleanOptionalAttributeType.TO_BE_COMPLETED, LevelAccessWheelchairAttributeType.YES, null, null, 1),
            new ErsadQuay(sloid + ":0:636655", null, BoardingDeviceAttributeType.NOT_APPLICABLE, null,
                BooleanOptionalAttributeType.TO_BE_COMPLETED, LevelAccessWheelchairAttributeType.YES, null, null, 1)),
        List.of(
            new ErsadContactPoint(sloid + ":1234", ContactPointType.TICKET_COUNTER, "Billettschalter",
                "Mo-Fr 07:00-18:30", StandardAttributeType.YES, StandardAttributeType.YES, 1),
            new ErsadContactPoint(sloid + ":9999", ContactPointType.INFORMATION_DESK, "Infoschalter",
                "Mo-Fr 08:00-17:00", StandardAttributeType.YES, StandardAttributeType.YES, 1)),
        List.of(new ErsadToilet(sloid + ":617340", "Toilette", StandardAttributeType.YES, 1)),
        List.of(new ErsadParking(sloid + ":parking:1", "Parkplatz Bahnhof", YES, YES, 1)),
        List.of(
            new ErsadPathLink(sloid + ":ref:1", sloid + ":0:3", ReferencePointElementType.PLATFORM,
                StepFreeAccessAttributeType.YES, TactileVisualAttributeType.YES, StandardAttributeType.YES, 1),
            new ErsadPathLink(sloid + ":ref:1", sloid + ":617340", ReferencePointElementType.TOILET,
                StepFreeAccessAttributeType.YES, TactileVisualAttributeType.TO_BE_COMPLETED,
                StandardAttributeType.TO_BE_COMPLETED, 1)),
        1);
  }

  static List<ErsadStation> burgdorfYverdonBellinzona() {
    return List.of(burgdorf(), yverdon(), bellinzona());
  }

  private static ErsadStation burgdorf() {
    String sloid = "ch:1:sloid:8005";
    return station(sloid, 8508005, "Burgdorf", 7.62169447355, 47.06070072639, NetexTriState.FALSE,
        StandardAttributeType.TO_BE_COMPLETED, "269286",
        List.of(
            heightQuay(sloid + ":1:1", "1", null),
            heightQuay(sloid + ":1:11", "11", null),
            heightQuay(sloid + ":2:2", "2", null),
            heightQuay(sloid + ":2:3", "3", null),
            heightQuay(sloid + ":3:4", "4", null),
            heightQuay(sloid + ":3:5", "5", null)));
  }

  private static ErsadStation yverdon() {
    String sloid = "ch:1:sloid:4200";
    return station(sloid, 8504200, "Yverdon-les-Bains", 6.64094400151, 46.78154518698, NetexTriState.PARTIAL,
        StandardAttributeType.YES, "628513",
        List.of(
            heightQuay(sloid + ":1:1", "1", YVERDON_ADVICE),
            heightQuay(sloid + ":2:2", "2", null),
            heightQuay(sloid + ":2:3", "3", null),
            new ErsadQuay(sloid + ":0:619346", null, BoardingDeviceAttributeType.RAMPS, null, YES,
                LevelAccessWheelchairAttributeType.TO_BE_COMPLETED, null, null, 1),
            notApplicableQuay(sloid + ":0:534539"),
            notApplicableQuay(sloid + ":0:814902")));
  }

  private static ErsadStation bellinzona() {
    String sloid = "ch:1:sloid:5213";
    return station(sloid, 8505213, "Bellinzona", 9.02951851227, 46.19543179221, NetexTriState.PARTIAL,
        StandardAttributeType.YES, "308649",
        List.of(
            heightQuay(sloid + ":1:1", "1", null),
            heightQuay(sloid + ":2:2", "2", null),
            heightQuay(sloid + ":2:3", "3", null),
            heightQuay(sloid + ":3:4", "4", null),
            heightQuay(sloid + ":3:6", "6", null)));
  }

  private static ErsadStation station(String sloid, int number, String name, double longitude, double latitude,
      NetexTriState accessibleStation, StandardAttributeType assistance, String toiletSuffix, List<ErsadQuay> quays) {
    return new ErsadStation(sloid, number, name, longitude, latitude, null, null, List.of("TRAIN"), accessibleStation,
        new ErsadAssistance(assistance, null, null, BooleanOptionalAttributeType.TO_BE_COMPLETED,
            StandardAttributeType.TO_BE_COMPLETED, null),
        new ErsadStationEquipment(StandardAttributeType.YES, StandardAttributeType.YES, StandardAttributeType.YES,
            BooleanOptionalAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED,
            StandardAttributeType.TO_BE_COMPLETED),
        quays,
        List.of(),
        List.of(new ErsadToilet(sloid + ":" + toiletSuffix, "Toilette", StandardAttributeType.YES, 1)),
        List.of(),
        List.of(),
        1);
  }

  private static ErsadQuay heightQuay(String sloid, String designation, String advice) {
    return new ErsadQuay(sloid, designation, BoardingDeviceAttributeType.TO_BE_COMPLETED, advice, YES,
        LevelAccessWheelchairAttributeType.YES, 0.55, BooleanOptionalAttributeType.NO,
        1);
  }

  private static ErsadQuay notApplicableQuay(String sloid) {
    return new ErsadQuay(sloid, null, BoardingDeviceAttributeType.NOT_APPLICABLE, null,
        BooleanOptionalAttributeType.NO, LevelAccessWheelchairAttributeType.NOT_APPLICABLE, null, null, 1);
  }
}
