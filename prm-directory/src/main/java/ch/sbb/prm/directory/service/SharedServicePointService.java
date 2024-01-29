package ch.sbb.prm.directory.service;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.exception.ServicePointDoesNotExistException;
import ch.sbb.prm.directory.exception.TrafficPointElementDoesNotExistsException;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SharedServicePointService {

  private final SharedServicePointRepository sharedServicePointRepository;
  private final ObjectMapper objectMapper;

  public Optional<SharedServicePointVersionModel> findServicePoint(String sloid) {
    Optional<SharedServicePoint> servicePoint = sharedServicePointRepository.findById(sloid);
    return servicePoint.map(i -> {
      try {
        SharedServicePointVersionModel sharedServicePointVersion = objectMapper.readValue(i.getServicePoint(),
            SharedServicePointVersionModel.class);
        if (!sharedServicePointVersion.isStopPoint()) {
          log.info("StopPoint with sloid={} was found but is not a stopPoint at the moment", sloid);
          return null;
        }
        return sharedServicePointVersion;
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  public SharedServicePointVersionModel validateServicePointExists(String sloid) {
    Optional<SharedServicePointVersionModel> servicePoint = findServicePoint(sloid);
    if (servicePoint.isEmpty()) {
      throw new ServicePointDoesNotExistException(sloid);
    }
    return servicePoint.get();
  }

  public void validateTrafficPointElementExists(String servicePointSloid, String trafficPointSloid) {
    SharedServicePointVersionModel servicePoint = validateServicePointExists(servicePointSloid);

    if (!servicePoint.getTrafficPointSloids().contains(trafficPointSloid)) {
      throw new TrafficPointElementDoesNotExistsException(trafficPointSloid);
    }
  }
}
