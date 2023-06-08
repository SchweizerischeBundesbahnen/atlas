package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(enumAsRef = true, example = "IN_OPERATION")
@RequiredArgsConstructor
public enum ServicePointStatus implements CodeAndDesignations {

  TO_BE_REQUESTED(0, "Zu beantragen", "A demander", "Da richiedere", "To be requested"),
  REQUESTED(1, "Angefragt", "Demandé", "Richiesto", "Requested"),
  PLANNED(2, "Geplant", "Prévu", "Pianificato", "Planned"),
  IN_OPERATION(3, "In Betrieb", "En fonctionnement", "In funzione", "In operation"),
  TERMINATED(4, "Terminiert", "Terminé", "Terminato", "Terminated"),
  IN_POST_OPERATIONAL_PHASE(5, "Post operationale Phase", "En phase post-opératoire", "In fase post-operativa",
      "In post-operational phase"),
  HISTORICAL(6, "Historisch", "Historique", "Storico", "Historical"),
  UNKNOWN(7, "Unbekannt", "Inconnu", "Sconosciuto", "Unknown");

  private final Integer id;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;

  public static ServicePointStatus from(Integer id) {
    return Arrays.stream(ServicePointStatus.values())
        .filter(servicePointStatus -> Objects.equals(servicePointStatus.getId(), id)).findFirst().orElse(null);
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
