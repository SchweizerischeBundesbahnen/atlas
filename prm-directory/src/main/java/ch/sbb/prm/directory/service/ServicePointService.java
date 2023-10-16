package ch.sbb.prm.directory.service;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.prm.directory.entity.ServicePoint;
import ch.sbb.prm.directory.exception.ServicePointDoesNotExistsException;
import ch.sbb.prm.directory.exception.TrafficPointElementDoesNotExistsException;
import ch.sbb.prm.directory.repository.ServicePointRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicePointService {

  private final ServicePointRepository servicePointRepository;
  private final ObjectMapper objectMapper;

  public Optional<SharedServicePointVersionModel> findServicePoint(String sloid) {
    Optional<ServicePoint> servicePoint = servicePointRepository.findById(sloid);
    return servicePoint.map(i -> {
      try {
        return objectMapper.readValue(i.getServicePoint(), SharedServicePointVersionModel.class);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  public SharedServicePointVersionModel validateServicePointExists(String sloid) {
    Optional<SharedServicePointVersionModel> servicePoint = findServicePoint(sloid);
    if (servicePoint.isEmpty()) {
      throw new ServicePointDoesNotExistsException(sloid);
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
