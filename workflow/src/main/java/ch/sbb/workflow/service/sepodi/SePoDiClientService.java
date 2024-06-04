package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.workflow.client.SePoDiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SePoDiClientService {

  private final SePoDiClient sePoDiClient;

  void updateStoPointStatusToInReview(String sloid, Long versionId) {
    UpdateServicePointVersionModel updateServicePointVersionModel = sePoDiClient.postServicePointsStatusUpdate(
        sloid, versionId, Status.IN_REVIEW);
    if (updateServicePointVersionModel != null && Status.IN_REVIEW != updateServicePointVersionModel.getStatus()) {
      throw new IllegalStateException("Something went wrong! The StopPoint Status must be " + Status.IN_REVIEW);
    }
  }

}
