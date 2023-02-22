package ch.sbb.workflow.service;

import ch.sbb.atlas.api.client.line.workflow.LineWorkflowClient;
import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyNotInReviewException;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.mapper.PersonMapper;
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
    workflow.setExaminant(PersonMapper.toEntity(examinantWorkflowCheckModel.getExaminant()));
    workflow.setStatus(
        examinantWorkflowCheckModel.isAccepted() ? WorkflowStatus.APPROVED : WorkflowStatus.REJECTED);

    processWorkflowOnLidi(workflow);
    notificationService.sendEventToMail(workflow);
    return workflow;
  }

  WorkflowStatus processWorkflowOnLidi(Workflow workflow) {
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(workflow.getId())
        .businessObjectId(workflow.getBusinessObjectId())
        .workflowStatus(workflow.getStatus())
        .workflowType(workflow.getWorkflowType())
        .build();
    if (workflowEvent.getWorkflowType() == WorkflowType.LINE) {
      return lineWorkflowClient.processWorkflow(workflowEvent);
    }
    throw new UnsupportedOperationException();
  }

  private boolean hasWorkflowInProgress(Long businessObjectId) {
    return !repository.findAllByBusinessObjectIdAndStatus(businessObjectId, WorkflowStatus.STARTED).isEmpty();
  }
}
