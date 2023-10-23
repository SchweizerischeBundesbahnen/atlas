package ch.sbb.atlas.servicepoint.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "BUS")
@Getter
@RequiredArgsConstructor
public enum MeanOfTransport implements CodeAndDesignations {

  TRAIN(0, "Z", "Zug", "Zug", "Train", "Train", "Treno"),
  BUS(1, "B", "Bus", "Bus", "Bus", "Bus", "Bus"),
  TRAM(2, "T", "Tram", "Tram", "Tram", "Tram", "Tram"),
  BOAT(3, "S", "Schiff", "Schiff", "Bateau", "Boat", "Nave"),
  CABLE_CAR(4, "L", "Kabinenbahn", "Kabinenbahn", "Télécabine", "Cable Car", "Cabinovia"),
  CHAIRLIFT(5, "E", "Sesselbahn", "Sesselbahn", "Télésiège", "Chairlift", "Seggiovia"),
  CABLE_RAILWAY(6, "N", "Standseilbahn", "Standseilbahn", "Funiculaire", "Cable Railway", "Funicolare"),
  RACK_RAILWAY(7, "H", "Zahnradbahn", "Zahnradbahn", "Chemin de fer à crémaillère", "Rack Railway", "Ferrovia a cremagliera"),
  METRO(8, "M", "Metro", "Metro", "Métro", "Metro", "Metro"),
  ELEVATOR(9, "A", "Aufzug", "Aufzug", "Ascenseur", "Elevator", "Ascensore"),
  UNKNOWN(10, "U", "Unbekannt", "Unbekannt", "Inconnu", "Unknown", "Sconosciute");

  private final Integer rank;
  private final String code;
  private final String name;
  private final String designationDe;
  private final String designationFr;
  private final String designationEn;
  private final String designationIt;

  public static MeanOfTransport from(String code) {
    return Arrays.stream(MeanOfTransport.values()).filter(meanOfTransport -> Objects.equals(meanOfTransport.getCode(), code))
            .findFirst().orElse(null);
  }

  public static Set<MeanOfTransport> fromCode(
      String meansOfTransportCode) {
    return Arrays.stream(Objects.nonNull(meansOfTransportCode) ? meansOfTransportCode.split("~") : new String[]{})
        .map(MeanOfTransport::from)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}
