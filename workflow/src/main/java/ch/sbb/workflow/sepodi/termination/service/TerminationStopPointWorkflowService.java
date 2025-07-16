package ch.sbb.workflow.sepodi.termination.service;

import static ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus.STARTED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.exception.TerminationDateBeforeException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationDecisionMapper;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
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

    ReadServicePointVersionModel readServicePointVersionModel = sePoDiAdminClient.postStartServicePointTermination(
        model.getSloid(), model.getVersionId(), terminationServicePointModel);

    TerminationStopPointWorkflow terminationStopPointWorkflow = populateWorkflow(
        model, readServicePointVersionModel);

    notificationService.sendStartTerminationNotificationToInfoPlus(terminationStopPointWorkflow);
    notificationService.sendStartConfirmationTerminationNotificationToApplicantMail(terminationStopPointWorkflow);
    return repository.save(terminationStopPointWorkflow);
  }

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflowBySloid(String sloid) {
    return repository.findTerminationStopPointWorkflowBySloid(sloid).orElseThrow(() -> new SloidNotFoundException(sloid));
  }

  @Redacted
  public Page<TerminationStopPointWorkflow> getTerminationWorkflows(
      TerminationStopPointWorkflowSearchRestrictions searchRestrictions) {
    return repository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public TerminationStopPointWorkflow addDecisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    if (terminationWorkflow.getStatus() != STARTED) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(STARTED);
    }
    if (decisionModel.getTerminationDate().isBefore(terminationWorkflow.getBoTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(), terminationWorkflow.getBoTerminationDate());
    }
    terminationWorkflow.setInfoPlusDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setInfoPlusTerminationDate(decisionModel.getTerminationDate());

    if (decisionModel.getJudgement() == JudgementType.YES) {
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
      notificationService.sendTerminationApprovedNotificationToNova(terminationWorkflow, decisionModel);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      postStopServicePointTermination(terminationWorkflow.getSloid(), terminationWorkflow.getVersionId());
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_NOT_APPROVED);
      notificationService.sendCancelNotificationToApplicationMail(terminationWorkflow, decisionModel);
    }
    return repository.save(terminationWorkflow);
  }

  public TerminationStopPointWorkflow addDecisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    if (!Set.of(TerminationWorkflowStatus.TARIFF_STOP_APPROVED, TerminationWorkflowStatus.TERMINATION_NOT_APPROVED).contains(terminationWorkflow.getStatus())) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    }
    if (decisionModel.getTerminationDate().isBefore(terminationWorkflow.getInfoPlusTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(), terminationWorkflow.getInfoPlusTerminationDate());
    }
    terminationWorkflow.setNovaDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setNovaTerminationDate(decisionModel.getTerminationDate());

    if (decisionModel.getJudgement() == JudgementType.YES) {
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
    }
    return repository.save(terminationWorkflow);
  }

  private ReadServicePointVersionModel postStopServicePointTermination(String sloid, Long id) {
    return sePoDiAdminClient.postStopServicePointTermination(sloid, id);
  }

  private TerminationStopPointWorkflow populateWorkflow(StartTerminationStopPointWorkflowModel model,
      ReadServicePointVersionModel readServicePointVersionModel) {
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflowMapper.toEntityStart(model);
    terminationStopPointWorkflow.setDesignationOfficial(readServicePointVersionModel.getDesignationOfficial());
    terminationStopPointWorkflow.setSboid(readServicePointVersionModel.getBusinessOrganisation());
    terminationStopPointWorkflow.setStatus(STARTED);

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

}
