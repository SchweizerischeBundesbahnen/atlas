package ch.sbb.atlas.workflow.service;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseWorkflowProcessingService<T extends BaseVersion, Y extends BaseWorkflowEntity> {

  protected final JpaRepository<T, Long> objectVersionRepository;
  protected final JpaRepository<Y, Long> objectWorkflowRepository;

  @RunAsUser(fakeUserType = KAFKA)
  public void processWorkflow(WorkflowEvent workflowEvent) {
    T objectVersion = getObjectVerision(workflowEvent);
    evaluateWorkflowProcessingStatus(workflowEvent, objectVersion);
    Y objectVersionWorkflow = buildObjectVersionWorkflow(workflowEvent, objectVersion);
    objectWorkflowRepository.save(objectVersionWorkflow);
    log.info("Workflow entity saved: {}", objectVersionWorkflow);
    objectVersionRepository.save(objectVersion);
    log.info("Object entity saved: {}", objectVersion);
  }

  void evaluateWorkflowProcessingStatus(WorkflowEvent workflowEvent, T objectVersion) {
    if (WorkflowStatus.STARTED == workflowEvent.getWorkflowStatus()) {
      objectVersion.setStatus(Status.IN_REVIEW);
      // CREATE SNAPHOT
      log.info("Changed Workflow status from {} to {}", Status.IN_REVIEW, Status.IN_REVIEW);
    } else {
      throw new IllegalStateException("Use case not yet implemented!!");
    }
  }

  T getObjectVerision(WorkflowEvent workflowEvent) {
    return objectVersionRepository.findById(workflowEvent.getBusinessObjectId())
        .orElseThrow(() -> new IdNotFoundException(workflowEvent.getBusinessObjectId()));
  }

  protected abstract Y buildObjectVersionWorkflow(WorkflowEvent workflowEvent, T object);

}
