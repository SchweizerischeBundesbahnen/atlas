package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.atlas.user.administration.security.redact.TthRedacted;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.mail.DossierNotificationService;
import ch.sbb.timetable.hearing.mapper.DossierMapper;
import ch.sbb.timetable.hearing.repository.DossierQuestionRepository;
import ch.sbb.timetable.hearing.repository.DossierRepository;
import ch.sbb.timetable.hearing.search.DossierSearchRestrictions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DossierService {

  private final DossierRepository dossierRepository;
  private final DossierQuestionRepository questionRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementService timetableHearingStatementService;
  private final DossierNotificationService notificationService;
  private final BoContactPermissionService boContactPermissionService;

  public List<Long> getStatementIdsFromDossierStatus(List<DossierStatus> dossierStatus) {
    return dossierRepository.findStatementIdsByDossierStatusIn(dossierStatus);
  }

  @Transactional
  public void updateDossierStatusClosingYear() {
    dossierRepository.updateDossierStatus(DossierStatus.CANCELED, List.of(DossierStatus.ADDED));
    dossierRepository.updateDossierStatus(DossierStatus.DISSOLVED,
        List.of(DossierStatus.MOVED, DossierStatus.DOSSIER_BO_CHECK, DossierStatus.DOSSIER_CANTON_CHECK));
  }

  @TthRedacted
  @PostAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING) || @boUserMailCheckService.isCurrentUserAssignedTo(returnObject)")
  public Dossier getDossierById(Long dossierId) {
    return findDossier(dossierId);
  }

  @TthRedacted
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING) || @boUserMailCheckService.isCurrentUserSbbUidAssignedTo(#searchRestrictions"
      + ".getRequestParams().getBoContactSbbuid())")
  public Page<Dossier> getDossiers(DossierSearchRestrictions searchRestrictions) {
    return dossierRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  @Transactional
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType)"
      + ".TIMETABLE_HEARING, #dossier)")
  public Dossier createDossier(Dossier dossier) {
    TimetableHearingYear activeHearingYear = timetableHearingYearService.getActiveHearingYear();

    checkPermissionForBoContactMailAndSetSbbuid(dossier);

    dossier.setDossierStatus(DossierStatus.ADDED);
    dossier.setTimetableYear(activeHearingYear.getTimetableYear());
    Dossier tthDossier = dossierRepository.saveAndFlush(dossier);
    updateStatements(DossierMapper.toBatchUpdateModel(dossier));
    return tthDossier;
  }

  @Transactional
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType)"
      + ".TIMETABLE_HEARING, #dossier)")
  public void sendDossierToBo(Dossier dossier) {
    dossier.setDossierStatus(DossierStatus.DOSSIER_BO_CHECK);

    notificationService.notifyBoAboutNewQuestion(dossier);

    dossierRepository.save(dossier);
  }

  @Transactional
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType)"
      + ".TIMETABLE_HEARING, #dossier)")
  public void completeDossier(Dossier dossier, DossierStatus status) {
    checkDossierIsInEditableStatus(dossier);
    if (!status.isAllowedForCompleteTransition()) {
      throw SimpleAtlasException.builder()
          .status(HttpStatus.BAD_REQUEST)
          .messageAndError("DossierStatus " + status + " is not completable")
          .build();
    }
    BatchUpdateTimetableHearingStatementsModel batchUpdateModel = DossierMapper.toBatchUpdateModel(dossier, status);
    dossier.setDossierStatus(status);
    dossierRepository.saveAndFlush(dossier);
    updateStatements(batchUpdateModel);
  }

  @Transactional
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType)"
      + ".TIMETABLE_HEARING, #dossier)")
  public Dossier updateDossier(Long dossierId, Dossier dossier) {
    Dossier currentDossier = getDossierById(dossierId);
    List<Long> previousStatementIds = new ArrayList<>(currentDossier.getStatementIds());
    checkDossierIsInEditableStatus(currentDossier);
    if (!dossier.getDossierStatus().isAllowedForUpdate()) {
      dossier.setDossierStatus(currentDossier.getDossierStatus());
    }
    checkPermissionForBoContactMailAndSetSbbuid(dossier);
    if (!Objects.equals(currentDossier.getDossierQuestions().getFirst().getAnswerToCanton(),
        dossier.getDossierQuestions().getFirst().getAnswerToCanton())) {
      throw SimpleAtlasException.builder()
          .status(HttpStatus.BAD_REQUEST)
          .messageAndError("Answer to canton must not be edited")
          .build();
    }

    dossier.setTimetableYear(currentDossier.getTimetableYear());

    Dossier updatedDossier = dossierRepository.saveAndFlush(dossier);
    updateRemovedStatements(previousStatementIds, dossier);
    updateStatements(DossierMapper.toBatchUpdateModel(updatedDossier));
    return updatedDossier;
  }

  private void updateStatements(BatchUpdateTimetableHearingStatementsModel batchUpdateModel) {
    timetableHearingStatementService.getTimetableHearingStatementsByIds(batchUpdateModel.getIds())
        .forEach(statement -> timetableHearingStatementService.updateStatementFromDossier(statement, batchUpdateModel));
  }

  private void updateRemovedStatements(List<Long> previousStatementIds, Dossier dossier) {
    List<Long> removedStatementIds = getRemovedStatementIds(previousStatementIds, dossier);
    if (!removedStatementIds.isEmpty()) {
      updateStatements(BatchUpdateTimetableHearingStatementsModel.builder()
          .ids(removedStatementIds)
          .dossierCanton(dossier.getSwissCanton())
          .statementStatus(StatementStatus.RECEIVED)
          .publicComment(dossier.getPublicComment())
          .internalComment(dossier.getInternalComment())
          .topic(dossier.getTopic())
          .build());
    }
  }

  private List<Long> getRemovedStatementIds(List<Long> previousStatementIds, Dossier updatedDossier) {
    previousStatementIds.removeAll(updatedDossier.getStatementIds());
    return previousStatementIds;
  }

  private void checkPermissionForBoContactMailAndSetSbbuid(Dossier dossier) {
    Optional<String> sbbuid = boContactPermissionService.checkPermissionForBoContactMail(dossier.getBoContactMail());
    sbbuid.ifPresent(dossier::setBoContactSbbuid);
  }

  private static void checkDossierIsInEditableStatus(Dossier dossier) {
    if (!dossier.getDossierStatus().isDossierEditable()) {
      throw SimpleAtlasException.builder()
          .status(HttpStatus.PRECONDITION_FAILED)
          .messageAndError("Dossier is not updatable in status " + dossier.getDossierStatus())
          .displayCode("TTH.DOSSIER_NOT_EDITABLE")
          .build();
    }
  }

  @Transactional
  @PreAuthorize("@boUserMailCheckService.isCurrentUserAssignedTo(#dossier)")
  public void answerQuestion(Long questionId, String boAnswer, Dossier dossier) {
    DossierQuestion question = questionRepository.findById(questionId).orElseThrow(() -> new IdNotFoundException(questionId));

    if (dossier.getDossierStatus() != DossierStatus.DOSSIER_BO_CHECK) {
      throw SimpleAtlasException.builder()
          .status(HttpStatus.PRECONDITION_FAILED)
          .messageAndError("Dossier is not in status DOSSIER_BO_CHECK")
          .displayCode("TTH.DOSSIER_NOT_IN_BO_CHECK_STATUS")
          .build();
    }

    dossier.setDossierStatus(DossierStatus.DOSSIER_CANTON_CHECK);
    dossierRepository.save(dossier);

    question.setAnswerToCanton(boAnswer);
    questionRepository.save(question);

    notificationService.notifyCantonAboutNewAnswer(dossier);
  }

  public Dossier getDossierByQuestionId(Long questionId) {
    return questionRepository.findByIdWithDossier(questionId).orElseThrow(() -> new IdNotFoundException(questionId))
        .getDossier();
  }

  public Dossier findDossier(Long id) {
    return dossierRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }
}
