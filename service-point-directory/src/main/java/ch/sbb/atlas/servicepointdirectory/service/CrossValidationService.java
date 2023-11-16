package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
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
  
}
