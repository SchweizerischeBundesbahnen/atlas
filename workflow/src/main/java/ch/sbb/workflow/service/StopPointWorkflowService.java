package ch.sbb.workflow.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private final StopPointWorkflowRepository repository;
  private final WorkflowNotificationService notificationService;

  public StopPointWorkflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<StopPointWorkflow> getWorkflows() {
    return repository.findAll();
  }

  public StopPointWorkflow startWorkflow(StopPointWorkflow stopPointWorkflow) {
    if (hasWorkflowInProgress(stopPointWorkflow.getVersionId())) {
      throw new BusinessObjectCurrentlyInReviewException();
    }
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    StopPointWorkflow entity = repository.save(stopPointWorkflow);
    if (entity.getStatus() == WorkflowStatus.STARTED) {

//      notificationService.sendEventToMail(entity);
    }
    return entity;
  }


  private boolean hasWorkflowInProgress(Long businessObjectId) {
    return !repository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.STARTED).isEmpty();
  }

}
