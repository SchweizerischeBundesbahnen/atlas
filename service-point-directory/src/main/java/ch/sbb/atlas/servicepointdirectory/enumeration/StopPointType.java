package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum StopPointType implements CodeAndDesignations {

  ORDERLY(10, "Ordentliche Haltestelle", "Arrêt ordinaire", "Fermata ordinaria", "Ordinary stop", "Ho", "Ao", "Fo", null),
  ON_REQUEST(20, "Bedarfshaltestelle", "Arrêt sur demande", "Fermata facoltativa", "Request stop", "Hb", "Ad", "Ff", null),
  ZONE_ON_REQUEST(30, "Bedarfshaltestelle (Gebiet)", "Arrêt sur demande (zone)", "Fermata facoltativa (zona)", "Request stop (area)", "Hg", "Ads",
      "Ffz", null),
  TEMPORARY(40, "Temporäre Haltestelle", "Arrêt temporaire", "Fermata temporanea", "Temporary stop", "Ht", "At", "Ft", null),
  OUT_OF_ORDER(50, "Haltestelle ausser Betrieb", "Arrêt hors service", "Fermata fuori servizio", "Station out of service", "Ha", "Ahs", "Ffs", null),
  UNKNOWN(0, "Nicht spezifiziert", "Non spécifié", "Non specificata", "Not specified", "Hns", "Ans", "Fns", null);

  private final Integer id;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;

  private final String abbreviationDe;
  private final String abbreviationFr;
  private final String abbreviationIt;
  private final String abbreviationEn;

  public static StopPointType from(Integer id) {
    return Arrays.stream(StopPointType.values()).filter(stopPointType -> Objects.equals(stopPointType.getId(), id)).findFirst()
        .orElse(null);
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
