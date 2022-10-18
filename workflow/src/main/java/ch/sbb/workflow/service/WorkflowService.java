package ch.sbb.workflow.service;

import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.entity.WorkflowStatus;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
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

  public Workflow createWorkflow(Workflow workflow) {
    workflow.setStatus(WorkflowStatus.ADDED);
    Workflow entity = repository.save(workflow);
    notificationService.sendEventToLidi(workflow);
    notificationService.sendEventToMail(workflow);
    return entity;
  }

  public Workflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<Workflow> getWorkflows() {
    return repository.findAll();
  }
}
