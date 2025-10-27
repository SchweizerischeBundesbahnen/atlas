package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.BaseSectorEntity;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.MissingTrainStopPointException;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorValidityException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.RevokedException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointStatusRevokedChangeNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service.TrafficPointElementService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectorValidationService {

  private final TrafficPointElementService trafficPointElementService;

  public void validateMeanOfTransportOfServicePoint(List<ServicePointVersion> servicePointVersions) {
    boolean isServicePointValid = servicePointVersions.stream()
        .anyMatch(servicePoint -> servicePoint.isStopPoint() && isMeanOfTransportOnlyTrain(servicePoint.getMeansOfTransport()));

    if (!isServicePointValid) {
      throw new MissingTrainStopPointException();
    }
  }

  private boolean isMeanOfTransportOnlyTrain(Set<MeanOfTransport> meansOfTransport) {
    return meansOfTransport != null
        && !meansOfTransport.isEmpty()
        && meansOfTransport.stream().allMatch(m -> m == MeanOfTransport.TRAIN);
  }

  public <T extends BaseSectorEntity> void validateValidity(T version) {
    List<TrafficPointElementVersion> trafficPointElementVersions =
        trafficPointElementService.findBySloidOrderByValidFrom(version.getTrafficPointSloid());

    TrafficPointElementVersion oldestVersion = trafficPointElementVersions.getFirst();
    TrafficPointElementVersion latestVersion = trafficPointElementVersions.getLast();

    DateRange validityTrafficPoint = DateRange.builder()
        .from(oldestVersion.getValidFrom())
        .to(latestVersion.getValidTo())
        .build();

    DateRange validitySector = DateRange.builder()
        .from(version.getValidFrom())
        .to(version.getValidTo())
        .build();

    if (!validitySector.isDateRangeContainedIn(validityTrafficPoint)) {
      throw new SectorValidityException(validityTrafficPoint);
    }
  }

  public  <T extends BaseSectorEntity>  void checkIfStatusRevoked(T version) {
    if (version.getStatus().equals(Status.REVOKED)) {
      throw new RevokedException(version.getSloid());
    }
  }
}
