package ch.sbb.workflow.service;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.Examinants;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private final StopPointWorkflowRepository repository;
  private final SePoDiClient sePoDiClient;

  private final Examinants examinants;
  private final WorkflowNotificationService notificationService;

  public StopPointWorkflow getWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<StopPointWorkflow> getWorkflows() {
    return repository.findAll();
  }

  public StopPointWorkflow addWorkflow(StopPointAddWorkflowModel stopPointAddWorkflowModel) {
    StopPointWorkflow stopPointWorkflow = mapStopPointWorkflow(stopPointAddWorkflowModel);
    if (hasWorkflowAdded(stopPointWorkflow.getVersionId())) {
      // TODO: WorkflowCurrentlyAddedException
      throw new IllegalStateException("Workflow already in Hearing!");
    }
    //TODO: extract me in a SePoDiService
    UpdateServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsImport(
            stopPointWorkflow.getVersionId(), Status.IN_REVIEW)
        .getBody();
    if (Objects.requireNonNull(updateServicePointVersionModel).getStatus() == Status.IN_REVIEW) {
      stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
      return repository.save(stopPointWorkflow);
    }
    throw new IllegalStateException("Something went wrong!");
  }

  public StopPointWorkflow startWorkflow(Long id) {
    StopPointWorkflow stopPointWorkflow = repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    // TODO: WorkflowCurrentlyInHearingException
    if (hasWorkflowHearing(stopPointWorkflow.getVersionId())) {
      throw new IllegalStateException("Workflow already in Hearing!");
    }
    // TODO: WorkflowCurrentlyAddedException
    if (stopPointWorkflow.getStatus() != WorkflowStatus.ADDED) {
      throw new IllegalStateException("Workflow status must be ADDED!!!");
    }
    stopPointWorkflow.setStatus(WorkflowStatus.HEARING);
    StopPointWorkflow workflow = repository.save(stopPointWorkflow);
    notificationService.sendStopPointWorkflowMail(workflow);
    return workflow;
  }

  private StopPointWorkflow mapStopPointWorkflow(StopPointAddWorkflowModel workflowStartModel) {
    ClientPersonModel examinantPersonByCanton = examinants.getExaminantPersonByCanton(workflowStartModel.getSwissCanton());
    ClientPersonModel examinantSpecialistOffice = examinants.getExaminantSpecialistOffice();
    List<ClientPersonModel> examinants = new ArrayList<>();
    examinants.add(examinantSpecialistOffice);
    examinants.add(examinantPersonByCanton);
    return StopPointWorkflowMapper.toEntity(workflowStartModel, examinants);
  }

  private boolean hasWorkflowAdded(Long businessObjectId) {
    return !repository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.ADDED).isEmpty();
  }

  private boolean hasWorkflowHearing(Long businessObjectId) {
    return !repository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.HEARING).isEmpty();
  }

}
