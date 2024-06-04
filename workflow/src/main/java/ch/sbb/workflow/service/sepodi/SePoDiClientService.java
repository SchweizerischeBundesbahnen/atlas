package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.Status;
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

  void updateStoPointStatusToInReview(StopPointWorkflow stopPointWorkflow) {
    UpdateServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.IN_REVIEW);
    if (updateServicePointVersionModel != null && Status.IN_REVIEW != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.IN_REVIEW, updateServicePointVersionModel.getStatus());
    }
  }

  void updateStoPointStatusToDraft(StopPointWorkflow stopPointWorkflow) {
    UpdateServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        stopPointWorkflow.getSloid(), stopPointWorkflow.getVersionId(), Status.DRAFT);
    if (updateServicePointVersionModel != null && Status.DRAFT != updateServicePointVersionModel.getStatus()) {
      throw new SePoDiClientWrongStatusReturnedException(Status.DRAFT, updateServicePointVersionModel.getStatus());
    }
  }
}
