package ch.sbb.workflow.lidi.line.service;

import ch.sbb.atlas.api.client.line.workflow.LineWorkflowClient;
import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.lidi.line.entity.LineWorkflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyNotInReviewException;
import ch.sbb.workflow.lidi.line.mail.LineWorkflowNotificationService;
import ch.sbb.workflow.mapper.PersonMapper;
import ch.sbb.workflow.lidi.line.repository.WorkflowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LineWorkflowService {

  private final WorkflowRepository repository;
  private final LineWorkflowNotificationService notificationService;
  private final LineWorkflowClient lineWorkflowClient;

  public LineWorkflow startWorkflow(LineWorkflow lineWorkflow) {
    if (hasWorkflowInProgress(lineWorkflow.getBusinessObjectId())) {
      throw new BusinessObjectCurrentlyInReviewException();
    }
    lineWorkflow.setStatus(WorkflowStatus.ADDED);
    LineWorkflow entity = repository.save(lineWorkflow);
    WorkflowStatus desiredWorkflowStatusByLidi = processWorkflowOnLidi(entity);
    entity.setStatus(desiredWorkflowStatusByLidi);
    if (entity.getStatus() == WorkflowStatus.STARTED) {
      notificationService.sendEventToMail(entity);
    }
    return entity;
  }

  @Redacted
  public LineWorkflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @Redacted
  public List<LineWorkflow> getWorkflows() {
    return repository.findAll();
  }

  public LineWorkflow examinantCheck(Long workflowId, ExaminantWorkflowCheckModel examinantWorkflowCheckModel) {
    LineWorkflow lineWorkflow = getWorkflow(workflowId);
    if (lineWorkflow.getStatus() != WorkflowStatus.STARTED) {
      throw new BusinessObjectCurrentlyNotInReviewException();
    }
    lineWorkflow.setCheckComment(examinantWorkflowCheckModel.getCheckComment());
    lineWorkflow.setExaminant(PersonMapper.toEntity(examinantWorkflowCheckModel.getExaminant()));
    lineWorkflow.setStatus(
        examinantWorkflowCheckModel.isAccepted() ? WorkflowStatus.APPROVED : WorkflowStatus.REJECTED);

    processWorkflowOnLidi(lineWorkflow);
    notificationService.sendEventToMail(lineWorkflow);
    return lineWorkflow;
  }

  WorkflowStatus processWorkflowOnLidi(LineWorkflow lineWorkflow) {
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(lineWorkflow.getId())
        .businessObjectId(lineWorkflow.getBusinessObjectId())
        .workflowStatus(lineWorkflow.getStatus())
        .workflowType(lineWorkflow.getWorkflowType())
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
