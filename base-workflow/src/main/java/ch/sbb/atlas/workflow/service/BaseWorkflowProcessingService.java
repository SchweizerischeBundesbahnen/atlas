package ch.sbb.atlas.workflow.service;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
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
  public void processWorkflow(LineWorkflowEvent lineWorkflowEvent) {
    T objectVersion = getObjectVersion(lineWorkflowEvent);
    evaluateWorkflowProcessingStatus(lineWorkflowEvent, objectVersion);
    Y objectVersionWorkflow = buildObjectVersionWorkflow(lineWorkflowEvent, objectVersion);
    objectWorkflowRepository.save(objectVersionWorkflow);
    log.info("Workflow entity saved: {}", objectVersionWorkflow);
    objectVersionRepository.save(objectVersion);
    log.info("Object entity saved: {}", objectVersion);
  }

  void evaluateWorkflowProcessingStatus(LineWorkflowEvent lineWorkflowEvent, T objectVersion) {
    Status preUpdateStatus = objectVersion.getStatus();

    switch (lineWorkflowEvent.getWorkflowStatus()){
      case STARTED -> objectVersion.setStatus(Status.IN_REVIEW);
      case APPROVED -> objectVersion.setStatus(Status.VALIDATED);
      case REJECTED -> objectVersion.setStatus(Status.DRAFT);
      default -> throw new IllegalStateException("Use case not yet implemented!!");
    }

    //TODO: ATLAS-894: Create Snapshot on start/approved/rejected
    log.info("Changed Object status from {} to {}", preUpdateStatus, objectVersion.getStatus());
  }

  T getObjectVersion(LineWorkflowEvent lineWorkflowEvent) {
    return objectVersionRepository.findById(lineWorkflowEvent.getBusinessObjectId())
        .orElseThrow(() -> new IdNotFoundException(lineWorkflowEvent.getBusinessObjectId()));
  }

  protected abstract Y buildObjectVersionWorkflow(LineWorkflowEvent lineWorkflowEvent, T object);

}
