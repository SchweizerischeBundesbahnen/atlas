package ch.sbb.line.directory.service.workflow;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;
import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.getProcessingStatus;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.service.BaseWorkflowProcessingService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LineWorkflowProcessingService extends BaseWorkflowProcessingService<LineVersion, LineVersionWorkflowEntity> {

  public LineWorkflowProcessingService(JpaRepository<LineVersion, Long> objectRepository,
      JpaRepository<LineVersionWorkflowEntity, Long> objectWorkflowRepository) {
    super(objectRepository, objectWorkflowRepository);
  }

  @RunAsUser(fakeUserType = KAFKA)
  public void processLineWorkflow(WorkflowEvent workflowEvent) {
    log.info("Started Workflow processing: {}", workflowEvent);
    processWorkflow(workflowEvent);
    log.info("Ended Workflow processing: {}", workflowEvent);
  }

  @Override
  protected LineVersionWorkflowEntity buildObjectVersionWorkflow(WorkflowEvent workflowEvent, LineVersion object) {
    WorkflowProcessingStatus workflowProcessingStatus = getProcessingStatus(workflowEvent.getWorkflowStatus());

    return LineVersionWorkflowEntity.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .lineVersion(object)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }
}
