package ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service;

import ch.sbb.atlas.servicepoint.SloidValidation;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.exception.DesignationOperationalMaxDigitsException;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficPointElementValidationService {

  static final int MAX_DIGITS_WITH_MEAN_OF_TRANSPORT_TRAIN = 3;
  static final int MAX_DIGITS_WITHOUT_MEAN_OF_TRANSPORT_TRAIN = 2;

  private final CrossValidationService crossValidationService;

  public void validatePreconditionBusinessRules(TrafficPointElementVersion trafficPointElementVersion) {
    crossValidationService.validateServicePointNumberExists(trafficPointElementVersion.getServicePointNumber());

    int expectedAmountOfColons =
        trafficPointElementVersion.getTrafficPointElementType() == TrafficPointElementType.BOARDING_AREA ?
            SloidValidation.EXPECTED_COLONS_AREA : SloidValidation.EXPECTED_COLONS_PLATFORM;
    SloidValidation.isSloidValid(trafficPointElementVersion.getSloid(), expectedAmountOfColons,
        trafficPointElementVersion.getServicePointNumber());
  }

  public void validateDesignationOperational(TrafficPointElementVersion trafficPointElementVersion) {
    String designationOperational = trafficPointElementVersion.getDesignationOperational();
    if (StringUtils.isBlank(designationOperational)) {
      return;
    }

    int maxDigits = crossValidationService.hasAnyVersionMeanOfTransportTrain(
        trafficPointElementVersion.getServicePointNumber()) ? MAX_DIGITS_WITH_MEAN_OF_TRANSPORT_TRAIN
        : MAX_DIGITS_WITHOUT_MEAN_OF_TRANSPORT_TRAIN;

    if (designationOperational.length() > maxDigits) {
      throw new DesignationOperationalMaxDigitsException(trafficPointElementVersion.getServicePointNumber(),
          designationOperational, maxDigits);
    }
  }

}
