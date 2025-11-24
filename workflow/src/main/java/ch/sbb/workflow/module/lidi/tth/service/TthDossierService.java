package ch.sbb.workflow.module.lidi.tth.service;

import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TthDossierService {

  private final TthDossierRepository dossierRepository;

  /**
   * Dossier:
   * - Thema
   * - Statements mit IDs
   * - Dossierstatus
   * - Frist zur Beantwortung
   * - Kommentare
   * (LIDI)
   * Statement:
   * - Statement
   * - Kantonskommentar
   * - Dossier-ID => wenn vorhanden editieren verboten => noch hinzufügen
   * (LIDI-WF-Communication)
   * Statusübergänge im Dossier -> Statements entsprechend updaten
   *
   * Mockups prüfen und attribute checken
   */
  public TthDossier createDossier(TthDossier dossier) {
    return dossierRepository.save(dossier);
  }

  /**
   * TU Sicht: jemand bekommt per Dossier&Mail eine Zuweisung
   * Wie kann die TU auf die Stellungnahme zugreifen?
   *
   *
   * question
   * antwort
   *
   * order brauchen wir nicht auf tu question
   */
}
