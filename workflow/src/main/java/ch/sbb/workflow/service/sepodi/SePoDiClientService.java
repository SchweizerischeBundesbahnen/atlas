package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.client.SePoDiAdminClient;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.SePoDiClientWrongStatusReturnedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
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

  public ReadServicePointVersionModel getServicePointById(Long id) {
    try {
      return sePoDiClient.getServicePointById(id);
    } catch (AtlasException e) {
      log.error("!!!! Something went wrong when calling SePoDi API: \n{}", e.getErrorResponse(), e);
      throw e;
    }
  }

  public ReadServicePointVersionModel updateStopPointStatusToDraft(StopPointWorkflow stopPointWorkflow) {
    ReadServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.DRAFT);
    if (updateServicePointVersionModel != null && Status.DRAFT != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.DRAFT, updateServicePointVersionModel.getStatus());
    }
    return updateServicePointVersionModel;
  }

  public ReadServicePointVersionModel updateStopPointStatusToValidatedAsAdmin(StopPointWorkflow stopPointWorkflow) {
    ReadServicePointVersionModel updateServicePointVersionModel = sePoDiAdminClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.VALIDATED);
    if (updateServicePointVersionModel != null && Status.VALIDATED != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.VALIDATED, updateServicePointVersionModel.getStatus());
    }
    return updateServicePointVersionModel;
  }

  public ReadServicePointVersionModel updateStopPointStatusToValidatedAsAdminForJob(StopPointWorkflow stopPointWorkflow) {
    try {
      return updateStopPointStatusToValidatedAsAdmin(stopPointWorkflow);
    } catch (AtlasException e) {
      log.error("!!!! Something went wrong: \n{}", e.getErrorResponse());
    }
    return null;
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
