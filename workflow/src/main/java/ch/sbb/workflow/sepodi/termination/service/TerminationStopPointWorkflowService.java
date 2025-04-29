package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationDecisionMapper;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointWorkflowService {

  private final TerminationStopPointWorkflowRepository repository;
  private final SePoDiAdminClient sePoDiAdminClient;
  private final TerminationStopPointNotificationService notificationService;

  public TerminationStopPointWorkflow startTerminationWorkflow(TerminationStopPointWorkflowModel model) {

    if (repository.existsTerminationStopPointWorkflowBySloid(model.getSloid())) {
      throw new IllegalStateException("Termination Stop Point workflow already exists");
    }
    ReadServicePointVersionModel readServicePointVersionModel = postServicePointTerminationInProgress(model.getSloid(),
        model.getVersionId());

    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflowMapper.toEntity(model);
    terminationStopPointWorkflow.setDesignationOfficial(readServicePointVersionModel.getDesignationOfficial());
    terminationStopPointWorkflow.setSboid(readServicePointVersionModel.getBusinessOrganisation());
    terminationStopPointWorkflow.setStatus(TerminationWorkflowStatus.STARTED);
    terminationStopPointWorkflow.setNovaTerminationDate(terminationStopPointWorkflow.getBoTerminationDate());
    terminationStopPointWorkflow.setInfoPlusTerminationDate(terminationStopPointWorkflow.getBoTerminationDate());

    notificationService.sendStartTerminationNotificationToInfoPlus(terminationStopPointWorkflow);
    notificationService.sendStartConfirmationTerminationNotificationToApplicantMail(terminationStopPointWorkflow);
    return repository.save(terminationStopPointWorkflow);
  }

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public TerminationStopPointWorkflow addDecisionInfoPlus(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    if (terminationWorkflow.getStatus() != TerminationWorkflowStatus.STARTED) {
      //TODO: create custom Exception
      throw new IllegalStateException("TerminationWorkflow Status must be STARTED");
    }
    if (decisionModel.getTerminationDate().isBefore(terminationWorkflow.getBoTerminationDate())) {
      //TODO: create custom Exception
      throw new IllegalStateException("The Termination Date cannot be before the Termination Date defined by the Business "
          + "Organisation");
    }
    terminationWorkflow.setInfoPlusDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setInfoPlusTerminationDate(decisionModel.getTerminationDate());
    if (decisionModel.getJudgement() == JudgementType.NO) {
      postServicePointTerminationInProgress(decisionModel.getSloid(), decisionModel.getVersionId());
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
      notificationService.sendCancelNotificationToApplicationMail(terminationWorkflow, decisionModel);
    }
    if (decisionModel.getJudgement() == JudgementType.YES) {
      postServicePointTerminationNotInProgress(decisionModel.getSloid(), decisionModel.getVersionId());
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
      notificationService.sendTerminationApprovedNotificationToNova(terminationWorkflow, decisionModel);
    }
    return repository.save(terminationWorkflow);
  }

  private ReadServicePointVersionModel postServicePointTerminationInProgress(String sloid,
      Long id) {
    return postServicePointTerminationStatusUpdate(sloid, id, true);
  }

  private ReadServicePointVersionModel postServicePointTerminationNotInProgress(String sloid,
      Long id) {
    return postServicePointTerminationStatusUpdate(sloid, id, false);
  }

  private ReadServicePointVersionModel postServicePointTerminationStatusUpdate(String sloid,
      Long id, boolean terminationInProgress) {
    return sePoDiAdminClient.postServicePointTerminationStatusUpdate(
        sloid, id, UpdateTerminationServicePointModel.builder().terminationInProgress(terminationInProgress).build());
  }

}
