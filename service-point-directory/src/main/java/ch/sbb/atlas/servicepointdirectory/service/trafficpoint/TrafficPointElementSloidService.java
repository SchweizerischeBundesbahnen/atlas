package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficPointElementSloidService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  public String getNextSloidForPlatform(ServicePointNumber servicePointNumber) {
    Long randomSixDigits = trafficPointElementVersionRepository.getNextRandomNumberForSloid();
    String result;
    do {
      result = "ch:1:sloid:" + servicePointNumber.getNumber() + ":0:" + randomSixDigits;
    } while (trafficPointElementVersionRepository.existsBySloid(result));
    return result;
  }
}
