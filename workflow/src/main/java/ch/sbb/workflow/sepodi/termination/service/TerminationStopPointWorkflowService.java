package ch.sbb.workflow.sepodi.termination.service;

import static ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus.CANCELED;
import static ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus.STARTED;
import static ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus.TERMINATION_NOT_APPROVED_CLOSED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.helper.TerminationHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.exception.TerminationDateBeforeException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.TerminationWorkflowHelper;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationDecisionMapper;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
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

  public TerminationStopPointWorkflow startTerminationWorkflow(StartTerminationStopPointWorkflowModel model) {

    if (!repository.findTerminationStopPointWorkflowBySloidAndVersionIdAndStatus(model.getSloid(), model.getVersionId(), STARTED)
        .isEmpty()) {
      throw new TerminationStopPointWorkflowAlreadyInStatusException(STARTED);
    }
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

  public TerminationStopPointWorkflow abortTerminationWorkflow(Long workflowId, TerminationAbortModel abortModel) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    if (terminationWorkflow.getStatus() == TerminationWorkflowStatus.STARTED) {
      terminationWorkflow.setStatus(CANCELED);
      notificationService.sendAbortNotificationToBoAndInfoPlus(terminationWorkflow, abortModel);
    }
    if (terminationWorkflow.getStatus() == TerminationWorkflowStatus.TARIFF_STOP_APPROVED) {
      terminationWorkflow.setStatus(CANCELED);
      notificationService.sendAbortNotificationToBoInfoPlusAndNova(terminationWorkflow, abortModel);
    }
    if (terminationWorkflow.getStatus() == TerminationWorkflowStatus.TERMINATION_NOT_APPROVED) {
      terminationWorkflow.setStatus(TERMINATION_NOT_APPROVED_CLOSED);
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
      checkDecisionTerminationDateWithinLastVersion(decisionModel.getTerminationDate(), terminationWorkflow);
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
      notificationService.sendTariffStopApprovedNotificationToNovaAndBo(terminationWorkflow);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      sePoDiAdminClient.stopServicePointTermination(terminationWorkflow.getSloid(), terminationWorkflow.getVersionId());
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_NOT_APPROVED);
      notificationService.sendTariffStopNotApprovedNotificationToBo(terminationWorkflow, decisionModel);
    }
    return repository.save(terminationWorkflow);
  }

  private static void checkInfoPlusDecisionPreconditions(TerminationDecisionModel decisionModel,
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

  public TerminationStopPointWorkflow addDecisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    checkNovaDecisionPreconditions(decisionModel, terminationWorkflow);

    terminationWorkflow.setNovaDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setNovaTerminationDate(decisionModel.getTerminationDate());

    if (decisionModel.getJudgement() == JudgementType.YES) {
      doApproveTermination(decisionModel, terminationWorkflow);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      doNotApproveTermination(terminationWorkflow);
    }
    return repository.save(terminationWorkflow);
  }

  private static void checkNovaDecisionPreconditions(TerminationDecisionModel decisionModel,
      TerminationStopPointWorkflow terminationWorkflow) {
    if (!Set.of(TerminationWorkflowStatus.TARIFF_STOP_APPROVED, TerminationWorkflowStatus.TERMINATION_NOT_APPROVED)
        .contains(terminationWorkflow.getStatus())) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    }
    if (decisionModel.getTerminationDate() != null && decisionModel.getTerminationDate()
        .isBefore(terminationWorkflow.getInfoPlusTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(),
          terminationWorkflow.getInfoPlusTerminationDate());
    }
  }

  private void doApproveTermination(TerminationDecisionModel decisionModel, TerminationStopPointWorkflow terminationWorkflow) {
    checkDecisionTerminationDateWithinLastVersion(decisionModel.getTerminationDate(), terminationWorkflow);
    if (terminationWorkflow.getStatus() != TerminationWorkflowStatus.TERMINATION_NOT_APPROVED
        && !terminationWorkflow.getInfoPlusTerminationDate().equals(terminationWorkflow.getNovaTerminationDate())) {
      sePoDiAdminClient.changeToTariffStop(
          StopPointWorkflowTerminationModel.builder()
              .sloid(terminationWorkflow.getSloid())
              .versionId(terminationWorkflow.getVersionId())
              .terminationDate(terminationWorkflow.getInfoPlusTerminationDate().plusDays(1))
              .build());
    }
    sePoDiAdminClient.terminateStopPoint(StopPointWorkflowTerminationModel.builder()
        .sloid(terminationWorkflow.getSloid())
        .versionId(terminationWorkflow.getVersionId())
        .terminationDate(terminationWorkflow.getNovaTerminationDate())
        .build());
    terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
  }

  private void doNotApproveTermination(TerminationStopPointWorkflow terminationWorkflow) {
    LocalDate terminationDate = TerminationWorkflowHelper.getTerminationDate(terminationWorkflow);
    sePoDiAdminClient.changeToTariffStop(
        StopPointWorkflowTerminationModel.builder()
            .sloid(terminationWorkflow.getSloid())
            .versionId(terminationWorkflow.getVersionId())
            .terminationDate(terminationDate.plusDays(1))
            .build()
    );
    terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
  }

}
