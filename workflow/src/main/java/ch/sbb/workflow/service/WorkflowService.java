package ch.sbb.workflow.service;

import ch.sbb.atlas.api.line.workflow.LineWorkflowEvent;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.workflow.api.ExaminantWorkflowCheckModel;
import ch.sbb.workflow.api.PersonModel;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyNotInReviewException;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.service.lidi.LineWorkflowClient;
import ch.sbb.workflow.workflow.WorkflowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class WorkflowService {

  private final WorkflowRepository repository;
  private final WorkflowNotificationService notificationService;
  private final LineWorkflowClient lineWorkflowClient;

  public Workflow startWorkflow(Workflow workflow) {
    if (hasWorkflowInProgress(workflow.getBusinessObjectId())) {
      throw new BusinessObjectCurrentlyInReviewException();
    }
    workflow.setStatus(WorkflowStatus.ADDED);
    Workflow entity = repository.save(workflow);
    WorkflowStatus desiredWorkflowStatusByLidi = processWorkflowOnLidi(entity);
    entity.setStatus(desiredWorkflowStatusByLidi);
    if (entity.getStatus() == WorkflowStatus.STARTED) {
      notificationService.sendEventToMail(entity);
    }
    return entity;
  }

  public Workflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<Workflow> getWorkflows() {
    return repository.findAll();
  }

  public Workflow examinantCheck(Long workflowId, ExaminantWorkflowCheckModel examinantWorkflowCheckModel) {
    Workflow workflow = getWorkflow(workflowId);
    if (workflow.getStatus() != WorkflowStatus.STARTED) {
      throw new BusinessObjectCurrentlyNotInReviewException();
    }
    workflow.setCheckComment(examinantWorkflowCheckModel.getCheckComment());
    workflow.setExaminant(PersonModel.toEntity(examinantWorkflowCheckModel.getExaminant()));
    workflow.setStatus(
        examinantWorkflowCheckModel.isAccepted() ? WorkflowStatus.APPROVED : WorkflowStatus.REJECTED);

    processWorkflowOnLidi(workflow);
    notificationService.sendEventToMail(workflow);
    return workflow;
  }

  WorkflowStatus processWorkflowOnLidi(Workflow workflow) {
    return lineWorkflowClient.processWorkflow(LineWorkflowEvent.builder()
        .workflowId(workflow.getId())
        .businessObjectId(workflow.getBusinessObjectId())
        .workflowStatus(workflow.getStatus())
        .build());
  }

  private boolean hasWorkflowInProgress(Long businessObjectId) {
    return !repository.findAllByBusinessObjectIdAndStatus(businessObjectId, WorkflowStatus.STARTED).isEmpty();
  }
}
