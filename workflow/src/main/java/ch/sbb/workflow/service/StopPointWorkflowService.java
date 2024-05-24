package ch.sbb.workflow.service;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.model.Examinants;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowService {

  private final StopPointWorkflowRepository repository;

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
// TODO: BusinessObjectCurrentlyAddedException
      throw new BusinessObjectCurrentlyInReviewException();
    }
    stopPointWorkflow.setStatus(WorkflowStatus.ADDED);
    return repository.save(stopPointWorkflow);
  }

  public StopPointWorkflow startWorkflow(StopPointWorkflow stopPointWorkflow) {
    if (hasWorkflowHearing(stopPointWorkflow.getVersionId())) {
      // TODO: BusinessObjectCurrentlyInHearingException
      throw new BusinessObjectCurrentlyInReviewException();
    }
    if(stopPointWorkflow.getStatus() != WorkflowStatus.ADDED){
      throw new IllegalStateException("Workflow status must be ADDED!!!");
    }
    //TODO: 1) set in SePoDi ServicePointVersion in status IN_REVIEW
    //      2) send mail notificationService.sendEventToMail(entity);
    return repository.save(stopPointWorkflow);
  }

  private StopPointWorkflow mapStopPointWorkflow(StopPointAddWorkflowModel workflowStartModel ){
    ClientPersonModel examinantPersonByCanton = examinants.getExaminantPersonByCanton(workflowStartModel.getSwissCanton());
    ClientPersonModel examinantSpecialistOffice = examinants.getExaminantSpecialistOffice();
    List<ClientPersonModel> examinants = new ArrayList<>();
    examinants.add(examinantSpecialistOffice);
    examinants.add(examinantPersonByCanton);
    return StopPointWorkflowMapper.toEntity(workflowStartModel,examinants);
  }

  private boolean hasWorkflowAdded(Long businessObjectId) {
    return !repository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.ADDED).isEmpty();
  }
  private boolean hasWorkflowHearing(Long businessObjectId) {
    return !repository.findAllByVersionIdAndStatus(businessObjectId, WorkflowStatus.HEARING).isEmpty();
  }

}
