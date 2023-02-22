package ch.sbb.atlas.workflow.service;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.base.service.model.workflow.WorkflowEvent;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import ch.sbb.atlas.workflow.model.AtlasVersionSnapshotable;
import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseWorkflowProcessingService<T extends BaseVersion, Y extends BaseWorkflowEntity,
    Z extends AtlasVersionSnapshotable> {

  protected final JpaRepository<T, Long> objectVersionRepository;
  protected final ObjectWorkflowRepository<Y> objectWorkflowRepository;
  protected final JpaRepository<Z, Long> objectVersionSnapshotRepository;

  public WorkflowStatus processWorkflow(WorkflowEvent workflowEvent,T objectVersion, Z versionSnapshot) {
    evaluateWorkflowProcessingStatus(workflowEvent, objectVersion, versionSnapshot);
    objectVersionRepository.save(objectVersion);
    log.info("Object entity saved: {}", objectVersion);
    Y objectVersionWorkflow = buildObjectVersionWorkflow(workflowEvent, objectVersion);
    objectWorkflowRepository.save(objectVersionWorkflow);
    log.info("Workflow entity saved: {}", objectVersionWorkflow);
    return workflowEvent.getWorkflowStatus() == WorkflowStatus.ADDED ? WorkflowStatus.STARTED : workflowEvent.getWorkflowStatus();
  }

  void evaluateWorkflowProcessingStatus(WorkflowEvent lineWorkflowEvent, T objectVersion, Z versionSnapshot) {
    Status preUpdateStatus = objectVersion.getStatus();

    if (preUpdateStatus != Status.REVOKED) {
      switch (lineWorkflowEvent.getWorkflowStatus()) {
        case ADDED -> objectVersion.setStatus(Status.IN_REVIEW);
        case APPROVED -> objectVersion.setStatus(Status.VALIDATED);
        case REJECTED -> objectVersion.setStatus(Status.DRAFT);
        default -> throw new IllegalStateException("Use case not yet implemented!!");
      }

      versionSnapshot.setStatus(objectVersion.getStatus());
      objectVersionSnapshotRepository.save(versionSnapshot);
      log.info("Changed Object status from {} to {}", preUpdateStatus, objectVersion.getStatus());
    }
  }

  public T getObjectVersion(WorkflowEvent lineWorkflowEvent) {
    return objectVersionRepository.findById(lineWorkflowEvent.getBusinessObjectId())
        .orElseThrow(() -> new IdNotFoundException(lineWorkflowEvent.getBusinessObjectId()));
  }

  protected abstract Y buildObjectVersionWorkflow(WorkflowEvent lineWorkflowEvent, T object);

}
