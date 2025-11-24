package ch.sbb.workflow.module.lidi.tth.service;

import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TthDossierService {

  private final TthDossierRepository dossierRepository;
  private final TimetableHearingStatementClient timetableHearingStatementClient;

  @Transactional
  public TthDossier createDossier(TthDossier dossier) {
    dossier.setDossierStatus(DossierStatus.ADDED);
    TthDossier tthDossier = dossierRepository.saveAndFlush(dossier);
    timetableHearingStatementClient.updateStatements(BatchUpdateTimetableHearingStatementsModel.builder()
        .ids(tthDossier.getStatementIds())
        .statementStatus(StatementStatus.IN_REVIEW)
        .dossierId(tthDossier.getId())
        .dossierContactMail(tthDossier.getBoContactMail())
        .build());
    return tthDossier;
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
