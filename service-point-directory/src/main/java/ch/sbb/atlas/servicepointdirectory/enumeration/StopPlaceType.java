package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum StopPlaceType {

  ORDERLY(10, "Ordentliche Haltestelle", "Arrêt ordinaire", "Fermata ordinaria", null, "Ho", "Ao", "Fo", null),
  ON_REQUEST(20, "Bedarfshaltestelle", "Arrêt sur demande", "Fermata facoltativa", null, "Hb", "Ad", "Ff", null),
  ZONE_ON_REQUEST(30, "Bedarfshaltestelle (Gebiet)", "Arrêt sur demande (zone)", "Fermata facoltativa (zona)", null, "Hg", "Ads",
      "Ffz", null),
  TEMPORARY(40, "Temporäre Haltestelle", "Arrêt temporaire", "Fermata temporanea", null, "Ht", "At", "Ft", null),
  OUT_OF_ORDER(50, "Haltestelle ausser Betrieb", "Arrêt hors service", "Fermata fuori servizio", null, "Ha", "Ahs", "Ffs", null),
  UNKNOWN(0, "Nicht spezifiziert", "Non spécifié", "Non specificata", null, "Hns", "Ans", "Fns", null);

  private final Integer id;
  private final String descriptionDe;
  private final String descriptionFr;
  private final String descriptionIt;
  private final String descriptionEn;

  private final String abbreviationDe;
  private final String abbreviationFr;
  private final String abbreviationIt;
  private final String abbreviationEn;

  public static StopPlaceType from(Integer id) {
    return Arrays.stream(StopPlaceType.values()).filter(el -> Objects.equals(el.id, id)).findFirst().orElse(null);
  }
}
