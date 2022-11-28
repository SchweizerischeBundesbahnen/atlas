package ch.sbb.line.directory.service.workflow;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import ch.sbb.atlas.workflow.service.BaseWorkflowProcessingService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;
import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.getProcessingStatus;

@Slf4j
@Service
@Transactional
public class LineWorkflowProcessingService extends BaseWorkflowProcessingService<LineVersion, LineVersionWorkflow> {

  public LineWorkflowProcessingService(JpaRepository<LineVersion, Long> objectRepository,
      ObjectWorkflowRepository<LineVersionWorkflow> objectWorkflowRepository) {
    super(objectRepository, objectWorkflowRepository);
  }

  @RunAsUser(fakeUserType = KAFKA)
  public void processLineWorkflow(LineWorkflowEvent lineWorkflowEvent) {
    log.info("Started Workflow processing: {}", lineWorkflowEvent);
    processWorkflow(lineWorkflowEvent);
    log.info("Ended Workflow processing: {}", lineWorkflowEvent);
  }

  @Override
  protected LineVersionWorkflow buildObjectVersionWorkflow(LineWorkflowEvent lineWorkflowEvent, LineVersion object) {
    Optional<LineVersionWorkflow> existingLineRelation = objectWorkflowRepository.findByWorkflowId(lineWorkflowEvent.getWorkflowId());
    WorkflowProcessingStatus workflowProcessingStatus = getProcessingStatus(lineWorkflowEvent.getWorkflowStatus());

    if (existingLineRelation.isPresent()) {
      existingLineRelation.get().setWorkflowProcessingStatus(workflowProcessingStatus);
      return existingLineRelation.get();
    }

    return LineVersionWorkflow.builder()
        .workflowId(lineWorkflowEvent.getWorkflowId())
        .lineVersion(object)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }
}
