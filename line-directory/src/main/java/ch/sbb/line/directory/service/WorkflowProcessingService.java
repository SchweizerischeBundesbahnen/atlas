package ch.sbb.line.directory.service;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.enumaration.WorkflowProcessingStatus;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkflowProcessingService {

  private final LineVersionWorkflowRepository lineVersionWorkflowRepository;
  private final LineVersionRepository lineVersionRepository;

  @RunAsUser(fakeUserType = KAFKA)
  public void processLineWorkflow(WorkflowEvent workflowEvent) {
    //TODO: exception handling:
    //1.if no line exists
    //2. if more than one WorkflowProcessingStatus.IN_PROGRESS in Progress
    LineVersion lineVersion =
        lineVersionRepository.findById(workflowEvent.getBusinessObjectId())
            .orElseThrow(() -> new IdNotFoundException(workflowEvent.getBusinessObjectId()));
    if (WorkflowStatus.STARTED == workflowEvent.getWorkflowStatus()) {
      lineVersion.setStatus(Status.IN_REVIEW);
      // CREATE SNAPHOT
      log.info("Started Workflow: {}", workflowEvent);
    }
    LineVersionWorkflow lineVersionWorkflow = getLineVersionWorkflow(workflowEvent, lineVersion);
    lineVersionWorkflowRepository.save(lineVersionWorkflow);
    log.info("Workflow entity saved: {}", lineVersionWorkflow);
    lineVersionRepository.save(lineVersion);
    log.info("LineVersion entity saved: {}", lineVersion);
  }

  private LineVersionWorkflow getLineVersionWorkflow(WorkflowEvent workflowEvent, LineVersion lineVersion) {
    WorkflowProcessingStatus workflowProcessingStatus = getWorkflowProcessingStatus(workflowEvent,
        lineVersion);

    return LineVersionWorkflow.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .lineVersion(lineVersion)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }

  private WorkflowProcessingStatus getWorkflowProcessingStatus(WorkflowEvent workflowEvent, LineVersion lineVersion) {
    WorkflowProcessingStatus workflowProcessingStatus = WorkflowProcessingStatus.getProcessingStatus(
        workflowEvent.getWorkflowStatus());
    //    if (workflowProcessingStatus == WorkflowProcessingStatus.IN_PROGRESS) {
    //      if (lineVersion.getLineVersionWorkflows().stream()
    //          .filter(l -> l.getWorkflowProcessingStatus() == WorkflowProcessingStatus.IN_PROGRESS).toList().size() > 0) {
    //        throw new IllegalStateException("TODO: define custom WorkflowException");
    //      }
    //    }
    return workflowProcessingStatus;
  }

}
