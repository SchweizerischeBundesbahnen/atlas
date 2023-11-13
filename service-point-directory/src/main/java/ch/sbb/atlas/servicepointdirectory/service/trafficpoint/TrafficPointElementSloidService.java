package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficPointElementSloidService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  public String getNextSloid(ServicePointNumber servicePointNumber, boolean isBoardingArea) {
    Long randomSixDigits = trafficPointElementVersionRepository.getNextRandomNumberForSloid();
    String suffix = isBoardingArea ? ":" + randomSixDigits : ":0:" + randomSixDigits;
    String result;

    do {
      result = "ch:1:sloid:" + getServicePointIdentifier(servicePointNumber) + suffix;
    } while (trafficPointElementVersionRepository.existsBySloid(result));
    return result;
  }

  private int getServicePointIdentifier(ServicePointNumber servicePointNumber) {
    return servicePointNumber.getCountry() == Country.SWITZERLAND ?
        servicePointNumber.getNumberShort() :
        servicePointNumber.getNumber();
  }
}
