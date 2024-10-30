package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrafficPointElementApiClient {

  private final TrafficPointElementApiV1 trafficPointElementApiV1;

  public void updateServicePoint(Long currentVersionId, CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    trafficPointElementApiV1.updateTrafficPoint(currentVersionId, trafficPointElementVersionModel);
  }

}
