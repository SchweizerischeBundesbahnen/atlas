package ch.sbb.workflow.service;

import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.workflow.api.ExaminantWorkflowCheckModel;
import ch.sbb.workflow.api.PersonModel;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.service.lidi.LineWorkflowClient;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.workflow.WorkflowRepository;
import java.util.List;
import java.util.Optional;
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

    workflow.setStatus(WorkflowStatus.ADDED);
    Workflow entity = repository.save(workflow);
    WorkflowStatus desiredWorkflowStatusByLidi = lineWorkflowClient.startWorkflow(null);
    entity.setStatus(desiredWorkflowStatusByLidi);
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
    workflow.setCheckComment(examinantWorkflowCheckModel.getCheckComment());
    workflow.setExaminant(PersonModel.toEntity(examinantWorkflowCheckModel.getExaminant()));
    workflow.setStatus(
        examinantWorkflowCheckModel.isAccepted() ? WorkflowStatus.APPROVED : WorkflowStatus.REJECTED);

    notificationService.sendEventToLidi(workflow);
    notificationService.sendEventToMail(workflow);
    return workflow;
  }

  public void processStartedEvent(Optional<Workflow> workflow) {
    if (workflow.isPresent()) {
      if (workflow.get().getStatus() == WorkflowStatus.STARTED) {
        notificationService.sendEventToMail(workflow.get());
      }
    }
  }
}
