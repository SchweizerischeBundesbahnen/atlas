package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepoint.SloidValidation;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficPointElementValidationService {

  private final CrossValidationService crossValidationService;

  public void validatePreconditionBusinessRules(TrafficPointElementVersion trafficPointElementVersion) {
    crossValidationService.validateServicePointNumberExists(trafficPointElementVersion.getServicePointNumber());

    int expectedAmountOfColons =
        trafficPointElementVersion.getTrafficPointElementType() == TrafficPointElementType.BOARDING_AREA ?
            SloidValidation.EXPECTED_COLONS_AREA : SloidValidation.EXPECTED_COLONS_PLATFORM;
    SloidValidation.isSloidValid(trafficPointElementVersion.getSloid(), expectedAmountOfColons,
        trafficPointElementVersion.getServicePointNumber());
  }

}
