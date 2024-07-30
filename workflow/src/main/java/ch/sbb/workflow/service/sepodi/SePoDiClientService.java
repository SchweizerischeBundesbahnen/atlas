package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.workflow.client.SePoDiAdminClient;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.SePoDiClientWrongStatusReturnedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SePoDiClientService {

  private final SePoDiClient sePoDiClient;
  private final SePoDiAdminClient sePoDiAdminClient;

  public ReadServicePointVersionModel updateStopPointStatusToInReview(String sloid, Long id) {
    ReadServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        sloid, id, Status.IN_REVIEW);
    if (updateServicePointVersionModel != null && Status.IN_REVIEW != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.IN_REVIEW, updateServicePointVersionModel.getStatus());
    }
    return updateServicePointVersionModel;
  }

  public ReadServicePointVersionModel updateStopPointStatusToDraft(StopPointWorkflow stopPointWorkflow) {
    ReadServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.DRAFT);
    if (updateServicePointVersionModel != null && Status.DRAFT != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.DRAFT, updateServicePointVersionModel.getStatus());
    }
    return updateServicePointVersionModel;
  }

  public void updateStopPointStatusToValidatedAsAdmin(StopPointWorkflow stopPointWorkflow) {
    ReadServicePointVersionModel updateServicePointVersionModel = sePoDiAdminClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.VALIDATED);
    if (updateServicePointVersionModel != null && Status.VALIDATED != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.VALIDATED, updateServicePointVersionModel.getStatus());
    }
  }

  public ReadServicePointVersionModel updateDesignationOfficialServicePoint(StopPointWorkflow stopPointWorkflow) {
    UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
        UpdateDesignationOfficialServicePointModel
            .builder()
            .designationOfficial(stopPointWorkflow.getDesignationOfficial())
            .build();

    return sePoDiClient.updateServicePointDesignationOfficial(stopPointWorkflow.getVersionId(),
        updateDesignationOfficialServicePointModel);
  }
}
