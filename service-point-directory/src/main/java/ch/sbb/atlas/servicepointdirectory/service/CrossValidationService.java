package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
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

  public boolean hasAnyVersionMeanOfTransportTrain(ServicePointNumber servicePointNumber) {
    return servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber).stream()
        .map(ServicePointVersion::getMeansOfTransport)
        .anyMatch(meansOfTransport -> meansOfTransport.contains(MeanOfTransport.TRAIN));
  }

}
