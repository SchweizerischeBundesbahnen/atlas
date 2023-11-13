package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrossValidationService {

  private final ServicePointService servicePointService;

  public void validateServicePointNumberExists(ServicePointNumber servicePointNumber) {
    if (!servicePointService.isServicePointNumberExisting(servicePointNumber)) {
      throw new ServicePointNumberNotFoundException(servicePointNumber);
    }
  }

  public void validateManuallyEnteredSloid(String sloid, TrafficPointElementType trafficPointElementType){
    int expectedColons = (trafficPointElementType.equals(TrafficPointElementType.BOARDING_AREA)) ? 4 : 5;
    String[] parts = sloid.split(":");

    if (parts.length != expectedColons + 1) {
      throw new SloidsNotEqualException("The SLOID does not correspond to the specified pattern");
    }

  }

}
