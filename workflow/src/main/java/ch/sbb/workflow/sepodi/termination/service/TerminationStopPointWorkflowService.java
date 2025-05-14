package ch.sbb.workflow.sepodi.termination.service;

import static ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus.STARTED;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.exception.TerminationDateBeforeException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationDecisionMapper;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import lombok.RequiredArgsConstructor;
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

    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflowMapper.toEntityStart(model);
    terminationStopPointWorkflow.setDesignationOfficial(readServicePointVersionModel.getDesignationOfficial());
    terminationStopPointWorkflow.setSboid(readServicePointVersionModel.getBusinessOrganisation());
    terminationStopPointWorkflow.setStatus(STARTED);
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
    if (terminationWorkflow.getStatus() != STARTED) {
      throw new TerminationStopPointWorkflowPreconditionStatusException(STARTED);
    }
    if (decisionModel.getTerminationDate().isBefore(terminationWorkflow.getBoTerminationDate())) {
      throw new TerminationDateBeforeException(decisionModel.getTerminationDate(), terminationWorkflow.getBoTerminationDate());
    }
    terminationWorkflow.setInfoPlusDecision(TerminationDecisionMapper.toEntity(decisionModel));
    terminationWorkflow.setInfoPlusTerminationDate(decisionModel.getTerminationDate());

    if (decisionModel.getJudgement() == JudgementType.YES) {
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
      notificationService.sendTerminationApprovedNotificationToNova(terminationWorkflow, decisionModel);
    }
    if (decisionModel.getJudgement() == JudgementType.NO) {
      postStopServicePointTermination(decisionModel.getSloid(), decisionModel.getVersionId());
      terminationWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
      notificationService.sendCancelNotificationToApplicationMail(terminationWorkflow, decisionModel);
    }
    return repository.save(terminationWorkflow);
  }

  public TerminationStopPointWorkflow addDecisionNova(TerminationDecisionModel decisionModel, Long workflowId) {
    TerminationStopPointWorkflow terminationWorkflow = getTerminationWorkflow(workflowId);
    //TODO add business logic
    throw new TerminationStopPointWorkflowPreconditionStatusException(STARTED);
  }

  private ReadServicePointVersionModel postStopServicePointTermination(String sloid, Long id) {
    return sePoDiAdminClient.postStopServicePointTermination(sloid, id);
  }

}
