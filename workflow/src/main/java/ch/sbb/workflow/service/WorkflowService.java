package ch.sbb.workflow.service;

import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.entity.WorkflowStatus;
import ch.sbb.workflow.kafka.MailProducerService;
import ch.sbb.workflow.kafka.WorkflowProducerService;
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

  private final WorkflowProducerService workflowProducerService;

  private final MailProducerService mailProducerService;

  public Workflow createWorkflow(Workflow workflow) {
    workflow.setStatus(WorkflowStatus.ADDED);
    Workflow entity = repository.save(workflow);
    sendEventToLidi(workflow);
    sendEventToMail(workflow);
    return entity;
  }

  private void sendEventToMail(Workflow workflow) {
    MailNotification mailNotification = MailNotification.builder().mailType(MailType.ATLAS_STANDARD)
        .to(List.of(workflow.getExaminant().getMail())).content("WF started").build();
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void sendEventToLidi(Workflow workflow) {
    WorkflowEvent workflowEvent = WorkflowEvent.builder().workflowId(workflow.getId()).build();
    workflowProducerService.produceWorkflowNotification(workflowEvent);
  }

  public Workflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<Workflow> getWorkflows() {
    return repository.findAll();
  }
}
