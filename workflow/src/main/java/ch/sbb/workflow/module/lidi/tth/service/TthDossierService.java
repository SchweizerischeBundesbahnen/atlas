package ch.sbb.workflow.module.lidi.tth.service;

import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TthDossierService {

  private final TthDossierRepository dossierRepository;
  private final TimetableHearingStatementClient timetableHearingStatementClient;

  public TthDossier getDossierById(Long dossierId) {
    return dossierRepository.findById(dossierId).orElseThrow(() -> new IdNotFoundException(dossierId));
  }

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

  @Transactional
  public TthDossier sendDossierToBo(Long dossierId, TthDossierQuestion tthDossierQuestion) {
    TthDossier tthDossier = dossierRepository.findById(dossierId).orElseThrow(() -> new IdNotFoundException(dossierId));
    tthDossierQuestion.setTthDossier(tthDossier);
    tthDossier.setDossierQuestions(new ArrayList<>(List.of(tthDossierQuestion)));
    tthDossier.setDossierStatus(DossierStatus.DOSSIER_BO_CHECK);
    return dossierRepository.save(tthDossier);
  }
}
