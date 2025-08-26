package ch.sbb.workflow.module.sepodi.termination.service;

import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.CANCELED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.STARTED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TARIFF_STOP_APPROVED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TARIFF_STOP_NOT_APPROVED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TERMINATION_APPROVED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TERMINATION_NOT_APPROVED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TERMINATION_NOT_APPROVED_CLOSED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.validateTerminationIsAbortible;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.validateWorkflowStatusTransition;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.helper.TerminationHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.aop.LoggingAspect;
import ch.sbb.workflow.aop.MethodLogged;
import ch.sbb.workflow.exception.TerminationDateBeforeException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.module.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.module.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.module.sepodi.termination.TerminationWorkflowHelper;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.module.sepodi.termination.mapper.TerminationDecisionMapper;
import ch.sbb.workflow.module.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.module.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointWorkflowService {

  private final TerminationStopPointWorkflowRepository repository;
  private final SePoDiAdminClient sePoDiAdminClient;
  private final TerminationStopPointNotificationService notificationService;

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflowBySloidAndInProgress(String sloid) {
    return repository.findTerminationStopPointWorkflowBySloidAndStatusIn(sloid, TerminationWorkflowStatus.WORKFLOW_IN_PROGRESS)
        .orElseThrow(() -> new SloidNotFoundException(sloid));
  }

  @Redacted
  public Page<TerminationStopPointWorkflow> getTerminationWorkflows(
      TerminationStopPointWorkflowSearchRestrictions searchRestrictions) {
    return repository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  @MethodLogged(workflowType = LoggingAspect.START_TERMINATION_WORKFLOW)
  public TerminationStopPointWorkflow startTerminationWorkflow(StartTerminationStopPointWorkflowModel model) {
    checkTerminationWorkflowAlreadyExists(model);
    UpdateTerminationServicePointModel terminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(true)
        .terminationDate(model.getBoTerminationDate())
        .build();

    ReadServicePointVersionModel readServicePointVersionModel = sePoDiAdminClient.startServicePointTermination(
        model.getSloid(), model.getVersionId(), terminationServicePointModel);

    TerminationStopPointWorkflow terminationStopPointWorkflow = populateWorkflow(
        model, readServicePointVersionModel);

    TerminationStopPointWorkflow savedTerminationWorkflow = repository.saveAndFlush(terminationStopPointWorkflow);
    notificationService.sendStartTerminationNotificationToInfoPlusAndBo(savedTerminationWorkflow);
    return savedTerminationWorkflow;
  }

  @MethodLogged(workflowType = LoggingAspect.ABORT_TERMINATION_WORKFLOW)
  public TerminationStopPointWorkflow abortTerminationWorkflow(Long workflowId, TerminationAbortModel abortModel) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    validateTerminationIsAbortible(terminationWorkflow.getStatus());
    if (terminationWorkflow.getStatus() == TerminationWorkflowStatus.STARTED) {
      abortStartedTermination(abortModel, terminationWorkflow);
    }
    if (terminationWorkflow.getStatus() == TerminationWorkflowStatus.TARIFF_STOP_APPROVED) {
      abortTariffStopApprovedTermination(abortModel, terminationWorkflow);
    }
    if (terminationWorkflow.getStatus() == TERMINATION_NOT_APPROVED) {
      abortTerminationNotApprovedTermination(terminationWorkflow);
    }
    terminationWorkflow.setAbortComment(abortModel.getAbortComment());
    sePoDiAdminClient.stopServicePointTermination(terminationWorkflow.getSloid(), terminationWorkflow.getVersionId());
    return repository.saveAndFlush(terminationWorkflow);
  }

  public TerminationStopPointWorkflow addDecisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    checkInfoPlusDecisionPreconditions(decisionModel, terminationWorkflow);

    terminationWorkflow.setInfoPlusDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setInfoPlusTerminationDate(decisionModel.getTerminationDate());
    if (decisionModel.getJudgement() == JudgementType.YES) {
      addDecisionInfoPlusYes(decisionModel, terminationWorkflow);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      addDecisionInfoPlusNo(decisionModel, terminationWorkflow);
    }
    return repository.save(terminationWorkflow);
  }

  @MethodLogged(workflowType = LoggingAspect.ADD_TERMINATION_DECISION_NOVA)
  public TerminationStopPointWorkflow addDecisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    checkNovaDecisionPreconditions(decisionModel, terminationWorkflow);

    terminationWorkflow.setNovaDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setNovaTerminationDate(decisionModel.getTerminationDate());

    if (decisionModel.getJudgement() == JudgementType.YES) {
      addDecisionNovaYes(decisionModel, terminationWorkflow);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      addDecisionNovaNo(terminationWorkflow);
    }
    return repository.save(terminationWorkflow);
  }

  private void checkTerminationWorkflowAlreadyExists(StartTerminationStopPointWorkflowModel model) {
    if (!repository.findTerminationStopPointWorkflowBySloidAndVersionIdAndStatus(model.getSloid(), model.getVersionId(), STARTED)
        .isEmpty()) {
      throw new TerminationStopPointWorkflowAlreadyInStatusException(STARTED);
    }
  }

  private void checkNovaDecisionPreconditions(TerminationDecisionModel decisionModel,
      TerminationStopPointWorkflow terminationWorkflow) {
    if (!Set.of(TerminationWorkflowStatus.TARIFF_STOP_APPROVED, TERMINATION_NOT_APPROVED)
        .contains(terminationWorkflow.getStatus())) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    }
    if (decisionModel.getTerminationDate() != null && decisionModel.getTerminationDate()
        .isBefore(terminationWorkflow.getInfoPlusTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(),
          terminationWorkflow.getInfoPlusTerminationDate());
    }
  }

  private TerminationStopPointWorkflow populateWorkflow(StartTerminationStopPointWorkflowModel model,
      ReadServicePointVersionModel readServicePointVersionModel) {
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflowMapper.toEntityStart(model);
    terminationStopPointWorkflow.setDesignationOfficial(readServicePointVersionModel.getDesignationOfficial());
    terminationStopPointWorkflow.setSboid(readServicePointVersionModel.getBusinessOrganisation());
    terminationStopPointWorkflow.setStatus(STARTED);
    terminationStopPointWorkflow.setVersionValidTo(readServicePointVersionModel.getValidTo());

    TerminationDecision infoPlusEmptyDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .build();
    terminationStopPointWorkflow.setInfoPlusDecision(infoPlusEmptyDecision);

    TerminationDecision novaEmptyDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .build();
    terminationStopPointWorkflow.setNovaDecision(novaEmptyDecision);
    return terminationStopPointWorkflow;
  }

  private void checkInfoPlusDecisionPreconditions(TerminationDecisionModel decisionModel,
      TerminationStopPointWorkflow terminationWorkflow) {
    if (terminationWorkflow.getStatus() != STARTED) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(STARTED);
    }
    if (decisionModel.getTerminationDate() != null && decisionModel.getTerminationDate()
        .isBefore(terminationWorkflow.getBoTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(), terminationWorkflow.getBoTerminationDate());
    }
  }

  private void checkDecisionTerminationDateWithinLastVersion(LocalDate terminationDate,
      TerminationStopPointWorkflow terminationWorkflow) {
    TerminationHelper.isValidToInLastVersionRange(terminationWorkflow.getSloid(),
        new DateRange(terminationWorkflow.getBoTerminationDate(), terminationWorkflow.getVersionValidTo()), terminationDate);
  }

  private void abortTerminationNotApprovedTermination(TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(TERMINATION_NOT_APPROVED, TERMINATION_NOT_APPROVED_CLOSED);
    terminationWorkflow.setStatus(TERMINATION_NOT_APPROVED_CLOSED);
  }

  private void abortTariffStopApprovedTermination(TerminationAbortModel abortModel,
      TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(TARIFF_STOP_APPROVED, CANCELED);
    terminationWorkflow.setStatus(CANCELED);
    notificationService.sendAbortNotificationToBoInfoPlusAndNova(terminationWorkflow, abortModel);
  }

  private void abortStartedTermination(TerminationAbortModel abortModel, TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(STARTED, CANCELED);
    terminationWorkflow.setStatus(CANCELED);
    notificationService.sendAbortNotificationToBoAndInfoPlus(terminationWorkflow, abortModel);
  }

  private void addDecisionInfoPlusNo(TerminationDecisionModel decisionModel, TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(STARTED, TARIFF_STOP_NOT_APPROVED);
    sePoDiAdminClient.stopServicePointTermination(terminationWorkflow.getSloid(), terminationWorkflow.getVersionId());
    terminationWorkflow.setStatus(TARIFF_STOP_NOT_APPROVED);
    notificationService.sendTariffStopNotApprovedNotificationToBo(terminationWorkflow, decisionModel);
  }

  private void addDecisionInfoPlusYes(TerminationDecisionModel decisionModel, TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(STARTED, TARIFF_STOP_APPROVED);
    checkDecisionTerminationDateWithinLastVersion(decisionModel.getTerminationDate(), terminationWorkflow);
    terminationWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    notificationService.sendTariffStopApprovedNotificationToNova(terminationWorkflow);
  }

  private void addDecisionNovaNo(TerminationStopPointWorkflow terminationWorkflow) {
    validateWorkflowStatusTransition(TARIFF_STOP_APPROVED, TERMINATION_NOT_APPROVED);
    doNotApproveTermination(terminationWorkflow);
    notificationService.sendTerminationConfirmedNotification(terminationWorkflow);
  }

  private void doNotApproveTermination(TerminationStopPointWorkflow terminationWorkflow) {
    LocalDate terminationDate = TerminationWorkflowHelper.getTerminationDate(terminationWorkflow);
    changeToTariffStop(terminationWorkflow, terminationDate);
    terminationWorkflow.setStatus(TERMINATION_NOT_APPROVED);
  }

  private void addDecisionNovaYes(TerminationDecisionModel decisionModel, TerminationStopPointWorkflow terminationWorkflow) {
    TerminationWorkflowStatus actualTerminationStatus = terminationWorkflow.getStatus();
    validateWorkflowStatusTransition(TARIFF_STOP_APPROVED, TERMINATION_APPROVED);
    doApproveTermination(decisionModel, terminationWorkflow);
    if (TERMINATION_NOT_APPROVED != actualTerminationStatus) {
      notificationService.sendTerminationConfirmedNotification(terminationWorkflow);
    }
  }

  private void doApproveTermination(TerminationDecisionModel decisionModel, TerminationStopPointWorkflow terminationWorkflow) {
    checkDecisionTerminationDateWithinLastVersion(decisionModel.getTerminationDate(), terminationWorkflow);
    if (terminationWorkflow.getStatus() != TERMINATION_NOT_APPROVED
        && !terminationWorkflow.getInfoPlusTerminationDate().equals(terminationWorkflow.getNovaTerminationDate())) {
      changeToTariffStop(terminationWorkflow, terminationWorkflow.getInfoPlusTerminationDate());
    }
    terminateStopPoint(terminationWorkflow);
    terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
  }

  private void changeToTariffStop(TerminationStopPointWorkflow terminationWorkflow, LocalDate terminationWorkflow1) {
    sePoDiAdminClient.changeToTariffStop(
        StopPointWorkflowTerminationModel.builder()
            .sloid(terminationWorkflow.getSloid())
            .versionId(terminationWorkflow.getVersionId())
            .terminationDate(terminationWorkflow1.plusDays(1))
            .build());
  }

  private void terminateStopPoint(TerminationStopPointWorkflow terminationWorkflow) {
    if (terminationWorkflow.getNovaTerminationDate().isBefore(terminationWorkflow.getVersionValidTo())) {
      sePoDiAdminClient.terminateStopPoint(StopPointWorkflowTerminationModel.builder()
          .sloid(terminationWorkflow.getSloid())
          .versionId(terminationWorkflow.getVersionId())
          .terminationDate(terminationWorkflow.getNovaTerminationDate())
          .build());
    }
  }
}
