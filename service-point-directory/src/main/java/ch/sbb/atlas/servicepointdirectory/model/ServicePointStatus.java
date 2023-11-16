package ch.sbb.atlas.servicepointdirectory.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum ServicePointStatus {

  TO_BE_REQUESTED(0, "Zu beantragen", "A demander", "Da richiedere", "To be requested"),
  REQUESTED(1, "Angefragt", "Demandé", "Richiesto", "Requested"),
  PLANNED(2, "Geplant", "Prévu", "Pianificato", "Planned"),
  IN_OPERATION(3, "In Betrieb", "En fonctionnement", "In funzione", "In operation"),
  TERMINATED(4, "Terminiert", "Terminé", "Terminato", "Terminated"),
  IN_POST_OPERATIONAL_PHASE(5, "Im Nachlauf", "En phase post-opératoire", "In fase post-operativa",
      "In post-operational phase"),
  HISTORICAL(6, "Historisch", "Historique", "Storico", "Historical");

  private final Integer id;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;

  public static ServicePointStatus from(Integer id) {
    return Arrays.stream(ServicePointStatus.values())
        .filter(servicePointStatus -> Objects.equals(servicePointStatus.getId(), id)).findFirst().orElse(null);
  }
}